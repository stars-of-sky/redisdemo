package com.rp.redisdemo.service.sys;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Create By yrp.
 * Date:2020/5/19
 */

@Slf4j
@Service
public class DatabaseBackup {
    private String BASE_BACKUP_PATH = "/cloud/backup";

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void backUpSchedule() {
        int flag = 3;//出错，自动尝试3次
        while (flag > 0) {
            //生成文件名
            String fileName = generateFileName(1);
            try {
                boolean flag1 = doSysDataBackupOut2File(fileName);
            } catch (Exception e) {
                log.error("auto backup has a error {}", e);
                flag--;
            }
        }
    }

    public void doSysDataBackup(HttpServletRequest request) {
        //查询用户信息
        String token = request.getHeader("X-Subject-Token");
//        UserRoleInfo userRoleInfo = TokenUtil.getUserRoleInFo(token, systemConfig.getAuthCernterUrl());

        //生成文件名
        String fileName = generateFileName(2);

        boolean flag = doSysDataBackupOut2File(fileName);

        if (flag) {

          /*  SysDataBackup sysDataBackup = new SysDataBackup();
            sysDataBackup.setFileName(fileName);
            sysDataBackup.setCreateUser(userRoleInfo.getUserCode());
            sysDataBackup.setBackupType(2);

            long result = sysBackupDao.insertSysDataBackupRecord(sysDataBackup);


            if (result < 1) {
                throw new BussinessException(String.valueOf(ErrorEnum.SERVER_EXCEPTION.getHttp_code()), "insert SysDataBackup failed ");
            }*/
        }
    }


    private String generateFileName(int type) {
        long time = System.currentTimeMillis();
        if (type == 1) {
            return "Auto_" + time + ".sql";
        } else {
            return "Manual_" + time + ".sql";
        }
    }


    public void doSysDataRestore(HttpServletRequest request, Map<String, Object> param) {
        // 查询用户信息
       /* String token = getToken(request);
        UserRoleInfo userRoleInfo = TokenUtil.getUserRoleInFo(token, authCenterUrl);
        String id = String.valueOf(param.get("id"));
        // 根据id查询出备份记录
        List<SysDataBackup> backupRecord = sysBackupMapper.getSysDataBackupInfos(param);
        if (CollectionUtils.isEmpty(backupRecord)) {
            throw new BasicException(String.valueOf(ErrorEnum.SERVER_EXCEPTION.getHttp_code()), "未找到备份记录");
        }
        SysDataBackup itemRecord = backupRecord.get(0);
        String fileName = itemRecord.getFileName();
        if (StringUtils.isBlank(fileName)) {
            throw new BasicException(String.valueOf(ErrorEnum.SERVER_EXCEPTION.getHttp_code()), "备份文件路径为空");
        }
        FileMapping backupFileMapping = resourceConfiguration.build(ResourceConfiguration.backupDir, fileName);
        String filePath = backupFileMapping.getLocal();
        */
        String filePath = "";
        String fileName = "";
        if (!new File(filePath).exists()) {
            log.error("未找到备份文件:{}", filePath);
//            throw new BasicException(String.valueOf(ErrorEnum.SERVER_EXCEPTION.getHttp_code()), "未找到备份文件");
        }
        // 根据文件执行还原
        String shell = getRestoreCommand("127.0.0.1", filePath, fileName);
        log.debug("还原命令:{}", shell);
        String result = RuntimeUtil.execForStr(shell);
        if (result != null && result.contains("ERROR")) {
            log.error("还原出错，详细错误信息如下");
            log.error(result);
            return;
        } else {
          /*  SysDataRestore sysDataRestore = new SysDataRestore();
            sysDataRestore.setBackupId(Long.valueOf(id));
            if (userRoleInfo == null) {
                sysDataRestore.setCreateUser(Constant.SYSTEM_USER_CODE);
            } else {
                sysDataRestore.setCreateUser(userRoleInfo.getUserCode());
            }
            try {
                sysBackupMapper.insertSysDataRestoreRecord(sysDataRestore);
            } catch (Exception e) {
                throw new BasicException(String.valueOf(ErrorEnum.SERVER_EXCEPTION.getHttp_code()),
                        "insert SysDataRestore failed ");
            }*/
        }
    }

    /**
     * 备份输出文件
     *
     * @param fileName
     * @return
     */
    public boolean doSysDataBackupOut2File(String fileName) {

        if (StringUtils.isEmpty(fileName)) {
            return false;
        }

        String osName = SystemUtil.getOsInfo().getName();

        log.info("current system is ----------> [{}]", osName);

        if (SystemUtil.getOsInfo().isLinux()) {
            if (!new File(BASE_BACKUP_PATH).exists()) {
                new File(BASE_BACKUP_PATH).mkdirs();
            }

            String commandForBackup = getBackupCommandForFile("127.0.0.1", "vsl_data_backup");

            execCommand(commandForBackup, BASE_BACKUP_PATH, fileName);
        }

        return true;
    }

    /**
     * 还原
     *
     * @param fileName
     * @return
     */
    public boolean doSysDataRestoreFromFile(String fileName) {

        if (StringUtils.isEmpty(fileName)) {
            return false;
        }

        String[] command = new String[3];

        if (SystemUtil.getOsInfo().isLinux()) {
            String filePath = (new File(System.getProperty("catalina.home"))).getPath() + File.separator + "sysdatabackup" + File.separator;

            if (!new File(filePath + fileName).exists()) {
                return false;
            }
            command[0] = "/bin/sh";
            command[1] = "-c";
            command[2] = getRestoreCommand("127.0.0.1", filePath, fileName);
        }

        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            log.error("Runtime.getRuntime().exec(command) error ----------> [{}]", e);
            return false;
        }

        return true;
    }

    /**
     * 生成备份命令
     *
     * @param ip
     * @param tables
     * @return
     */
    private String getBackupCommandForFile(String ip, String tables) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mysqldump -h").append(ip).append(" -p3306 ")
                .append("-uroot -pdahuacloud ").append("--set-charset=UTF-8 ").append("nextdb2 ").append(tables);

        return stringBuilder.toString();
    }

    /**
     * 生成还原命令
     *
     * @param ip
     * @param filePath
     * @param fileName
     * @return
     */
    private String getRestoreCommand(String ip, String filePath, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mysql -h").append(ip).append(" -uroot -pdahuacloud nextdb2 < ")
                .append(filePath).append(fileName);

        return stringBuilder.toString();
    }


    public void execCommand(String cmd, String filePath, String fileName) {

        try {
            String[] commond = {"/bin/sh", "-c", cmd};
//            String[] commond = {"cmd.exe","/c",cmd};
            // 执行命令, 返回一个子进程对象（命令在子进程中执行）
            Process process = Runtime.getRuntime().exec(commond);

            // 方法阻塞, 等待命令执行完成（成功会返回0）
            process.waitFor();

            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            // 读取输出

            File file = new File(filePath, fileName);

            if (!file.exists()) {
                boolean flag = file.createNewFile();
                if (!flag) {
                    throw new Exception("file.createNewFile() error");
                }
            }
            FileUtils.copyInputStreamToFile(process.getInputStream(), file);

            log.info("error out is ------> [{}]", IOUtils.toString(process.getErrorStream(), "UTF-8"));

        } catch (Exception e) {
            log.error("error -----> [{}]", e);
        }
    }


}
