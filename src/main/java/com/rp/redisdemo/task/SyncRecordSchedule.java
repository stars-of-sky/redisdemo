/*
package com.rp.redisdemo.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

*/
/**
 * Create By yrp.
 * Date:2020/4/16
 *//*


@Slf4j
@Service
public class SyncRecordSchedule {
//    public class StorageChannelServiceImpl extends BaseService implements StorageChannelService {

        @Autowired
        private StoragePlanMapper storagePlanMapper;
        @Autowired
        private RecordNodeRelationMapper recordNodeRelationMapper;

        @Autowired
        private TimeTemplateService timeTemplateService;

        @Autowired
        private RegionService regionService;

        @Autowired
        private CloudStorageService cloudStorageService;

        // 码流类型
        private final static String videoStreamMain = "main";
        private final static String videoStreamExtra1 = "extra1";
        private final static String videoStreamExtra2 = "extra2";
        LinkedBlockingQueue<RecordNode> queue = new LinkedBlockingQueue<RecordNode>();

        @PostConstruct
        public void init() {
            List<RecordNode> list = recordNodeRelationMapper.getRecordNodesForSync();
            if (!CollectionUtils.isEmpty(list)) {
                queue.addAll(list);
            }
        }

        @Override
        public void addRecordNodes(List<RecordNode> recordNodes) {
            try {
                List<RecordNode> filterList = recordNodes.stream().filter(e -> (storagePlanMapper.getRecordById(e.getStoragePlanId()) != null))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(filterList)) {
                    return;
                }
                recordNodeRelationMapper.insertNodeRelationInfoList(filterList);
                queue.addAll(recordNodes);
            } catch (Exception e) {
                log.error("添加录像计划关联通道信息出错，原因:{}", e);
            }
        }

    @Override
    public void updateRecordNodes(List<RecordNode> recordNodes) {
        try {

            // 获取更新的所有通道
            List<String> channels = recordNodes.stream().map(RecordNode::getNodeCode).collect(Collectors.toList());
            // 从库中查询已有的记录
            List<RecordNode> updateList = recordNodeRelationMapper.getRecordScheduleIdsByChannelCode(channels);
            // 过滤后实际需要更新的记录
            updateList = recordNodes.stream().filter(node -> (channels.contains(node.getNodeCode())
                    && storagePlanMapper.getRecordById(node.getStoragePlanId()) != null))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(updateList)) {
                return;
            }
            recordNodeRelationMapper.updateRecordScheduleChannels(updateList);
            // 新增的记录
            List<RecordNode> insertList = ListUtils.removeAll(recordNodes, updateList);
            if (!CollectionUtils.isEmpty(insertList)) {
                recordNodeRelationMapper.insertNodeRelationInfoList(insertList);
            }
            queue.addAll(recordNodes);
        } catch (Exception e) {
            log.error("更新通道和存储计划的关系异常，原因:{}", e);
        }
    }

    @Override
    public Integer countByPlanId(Long id) {
        // TODO Auto-generated method stub
        List<Integer> list = recordNodeRelationMapper.countByPlanId(id);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        } else {
            return list.get(0);
        }
    }

    @Override
    public List<RecordNode> getRecordScheduleAndChannelInfoById(Long id) throws Exception {
        return recordNodeRelationMapper.getRecordScheduleAndChannelInfoById(id);
    }

    @Override
    public RecordNode getRecordScheduleByChannelCode(String channelCode) {
        List<RecordNode> list = recordNodeRelationMapper.getRecordScheduleIdsByChannelCode(Arrays.asList(channelCode));
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    */
/**
     * 定时将存储任务同步到paas
     *//*

    @Scheduled(initialDelay = 20000L, fixedDelay = 1000 * 20)
    private void syncToPaas() {
        RecordNode recordNode = queue.poll();
        log.info("定时任务开始：");
        while (recordNode != null) {
            try {
                enableStoragePlan(recordNode);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("定时将存储任务同步到paas InterruptedException :{}", e);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                log.error("定时将存储任务同步到paas error:{}", e);
                try {
                    queue.put(recordNode);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            recordNode = queue.poll();
            log.info("queue.size:{},queue.poll():{}", queue.size(), new Gson().toJson(recordNode));
        }
    }

    @Override
    public void enableStoragePlan(RecordNode recordScheduleChannel) throws Exception {
        String channelCode = recordScheduleChannel.getNodeCode();
        Integer planStatus = recordScheduleChannel.getPlanStatus();
        Long planId = recordScheduleChannel.getStoragePlanId();
        if (StringUtil.isEmpty(channelCode) || null == planStatus || null == planId) {
            log.error("enableStoragePlan missing args");
            return;
        }
        boolean flag = false;
        // 2.如果启用/停用，调用paas接口启用/停用该通道的录像计划
        // 0-已同步,1- 新增未同步, 2-更新未同步,3:不同步,-1:删除未同步(逻辑删除)
        Integer isSync = recordScheduleChannel.getIsSync() == null ? 1 : recordScheduleChannel.getIsSync();
        // 状态:0:不启用，1:启用
        Integer stat = recordScheduleChannel.getPlanStatus() == null ? 1 : recordScheduleChannel.getPlanStatus();
        if ((isSync == 1 || isSync == 2) && stat == 1) {
            // 新增 or 重新配置录像计划
            flag = startStoragePlan(recordScheduleChannel);
        } else if (isSync == -1) {
            // 删除录像计划
            flag = stopStoragePlan(recordScheduleChannel);
        } else if (isSync != 1 && isSync != -1 && stat == 0) {
            // 停止录像计划
            flag = stopStoragePlan(recordScheduleChannel);
        }
        if (flag) {
            log.info("flag:{}", flag);
            // 判断同步类型，如果是需要删除的，则进行删除
        //...........

            if (isSync == -1) {
                recordNodeRelationMapper.deleteRecordScheduleChannel(recordScheduleChannel);
            } else {
                log.info("update isSync:{}", isSync);
                recordScheduleChannel.setIsSync(0);
                if (recordScheduleChannel.getVersion() == null) {
                    recordScheduleChannel.setVersion(0);
                } else {
                    recordScheduleChannel.setVersion(recordScheduleChannel.getVersion() + 1);
                }
                log.info("update recordScheduleChannel:{}", new Gson().toJson(recordScheduleChannel));
                recordNodeRelationMapper.updateRecordScheduleChannel(recordScheduleChannel);
            }
        }

        @Override
        public int updateStorageChannel(RecordNode recordNode) {
            int result = recordNodeRelationMapper.updateRelation(recordNode);
            if (result == -1) {
                return -1;
            }
            queue.add(recordNode);
//        updateRecordNodes(Collections.singletonList(recordNode));
            return result;
        }

        */
/**
         * 开启录像计划
         *
         * @param recordScheduleChannel
         *//*

        private boolean startStoragePlan(RecordNode recordScheduleChannel) throws Exception {
            String channelCode = recordScheduleChannel.getNodeCode();
            Long planId = recordScheduleChannel.getStoragePlanId();
            // 1.先查询录像计划
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", planId);
            List<RecordSchedule> list = storagePlanMapper.getRecordScheduleById(map);
            if (CollectionUtils.isEmpty(list)) {
                return false;
            }
            RecordSchedule recordSchedule = list.get(0);

            List<EncoderChannel> channelInfos = recordNodeRelationMapper.getChannelInfoByCode(channelCode);
            if (CollectionUtils.isEmpty(channelInfos)) {
                return false;
            }
            EncoderChannel channelInfo = channelInfos.get(0);
            String channelId = channelInfo.getChannelId();
            String paasId = channelInfo.getPaasId().toString();
            Long domainId = channelInfo.getDomainId();
            Region region = regionService.selectByPrimaryKey(paasId);
            // 使用云存储时下发下发存储配额
            if (region.getStorageType() == StorageType.EFS.ordinal()) {

                RecordQuotaPaasInfo condition = createRecordQuotaInfo(recordSchedule, region.getRegionId());
                RestResult result = cloudStorageService.updateQuotaInfo(channelId, condition, region, domainId);
                log.info("下发paas层录像计划信息，channelId：{}，paasInfo：{}，region：{}", channelId, JSON.toJSONString(condition),
                        JSON.toJSONString(region));
                if (!String.valueOf(HttpStatus.OK.value()).equals(result.getCode())) {
                    log.warn("请求paas层启用录像计划失败，将任务重新放回队列,channelCode={}", channelId);
                    // queue.put(recordScheduleChannel);
                    // return false;
                }
            }

            // 组装下发paas消息体
            RecordSchedulePaasInfo paasInfo = getRecordSchedulePaasInfoByScheduleInfo(recordSchedule);

            if (paasInfo != null) {
                log.info("下发paas层录像计划信息，channelId：{}，paasInfo：{}，region：{}", channelId, JSON.toJSONString(paasInfo),
                        JSON.toJSONString(region));
                // 下发paas层录像计划信息
                RestResult result = cloudStorageService.putRecordSchedule(channelId, paasInfo, region);
                if (!String.valueOf(HttpStatus.OK.value()).equals(result.getCode())) {
                    log.warn("请求paas层启用录像计划失败，将任务重新放回队列,channelCode={}", channelId);
                    queue.put(recordScheduleChannel);
                    return false;
                }
            }
            log.info("新增更新录像计划成功{}", recordSchedule.getChannelName());
            return true;
        }

        */
/**
         * 停止录像计划
         *
         * @throws Exception
         *//*

        private boolean stopStoragePlan(RecordNode recordScheduleChannel) throws Exception {
            // 调用paas接口停用该通道的录像计划
            String channelCode = recordScheduleChannel.getNodeCode();
            List<EncoderChannel> channelInfos = recordNodeRelationMapper.getChannelInfoByCode(channelCode);
            if (CollectionUtils.isEmpty(channelInfos)) {
                log.info("通道信息不存在，重新入队列{}", new Gson().toJson(recordScheduleChannel));
                return false;
            }
            EncoderChannel channelInfo = channelInfos.get(0);
            String channelId = channelInfo.getChannelId();
            String paasId = channelInfo.getPaasId().toString();
            Region region = regionService.selectByPrimaryKey(paasId);

            RestResult result = cloudStorageService.deleteRecordSchedule(channelId, region);
            if (!String.valueOf(HttpStatus.OK.value()).equals(result.getCode())) {
                log.warn("请求paas层删除录像计划失败，将任务重新放回队列,channelCode={}", channelId);
                queue.put(recordScheduleChannel);
                return false;
            }
            log.info("停止删除录像计划成功{}", recordScheduleChannel.getNodeCode());
            return true;
        }

        */
/**
         * 根据录像计划信息配置存储配额待下发
         *
         * @param scheduleInfo
         * @return
         * @throws Exception
         *//*

        private RecordQuotaPaasInfo createRecordQuotaInfo(RecordSchedule scheduleInfo, String regionId) throws Exception {
            RecordQuotaPaasInfo condition = new RecordQuotaPaasInfo();

            try {
                condition.setLifeCycle(scheduleInfo.getLifeCycle().longValue());
                // 如果录像计划的生命周期或可靠性配置都指定，则使用全局配额
                if ((scheduleInfo.getLifeCycle() == null || scheduleInfo.getLifeCycle() == 0)
                        && (scheduleInfo.getDataNum() == null || scheduleInfo.getDataNum() == 0)
                        && (scheduleInfo.getParityNum() == null || scheduleInfo.getParityNum() == 0)) {
                    condition.setEnableDefault(Boolean.TRUE);
                } else {
                    List<StorageReduncanceCap> list = recordNodeRelationMapper.selectByRegionId(regionId);
                }
                return condition;
            } catch (Exception e) {
                log.error("");
            }
        }

        */
/**
         * 封装下发的pass层信息
         *
         * @param scheduleInfo
         * @return
         *//*

        private RecordSchedulePaasInfo getRecordSchedulePaasInfoByScheduleInfo(RecordSchedule scheduleInfo) {
            RecordSchedulePaasInfo paasInfo = new RecordSchedulePaasInfo();
            // 录像计划名称
            paasInfo.setName(scheduleInfo.getName());
            // 码流类型
            if (scheduleInfo.getVideoStream() != null && scheduleInfo.getVideoStream() == 1) {
                paasInfo.setVideoStream(videoStreamExtra1);
            } else if (scheduleInfo.getVideoStream() != null && scheduleInfo.getVideoStream() == 2) {
                paasInfo.setVideoStream(videoStreamExtra2);
            } else {
                paasInfo.setVideoStream(videoStreamMain);
            }
            // 补录功能
            if (scheduleInfo.getEnableRestore() == 1) {
                paasInfo.setEnableRestore(true);
            } else {
                paasInfo.setEnableRestore(false);
            }
            // 描述
            paasInfo.setDesc(scheduleInfo.getDesc());

            // 获取时间模板信息
            TimeTemplate timeTemplate = timeTemplateService.getTimeTemplateById(scheduleInfo.getTimeTempId());
            log.info("模板timeTemplate，开始时间：{}， 结束时间：{}", timeTemplate.getBeginTime(), timeTemplate.getEndTime());
            if (timeTemplate != null) {
                // 每日信息转换 方法体内做非null判断
                timeTemplateService.convertPeriod(timeTemplate);
                // 获取待下发的时间模板信息
                TimeSchedulePaasInfo dayTimeInfo = timeTemplateService.parseTimeTemplateInfoForPaas(timeTemplate);

                // 时间模板信息
                paasInfo.setTimeSchedule(dayTimeInfo);
            }

            return paasInfo;
        }
//    }
}
*/
