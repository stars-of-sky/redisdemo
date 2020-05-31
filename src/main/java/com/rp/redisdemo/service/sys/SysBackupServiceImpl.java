/*
package com.rp.redisdemo.service.sys;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dahua.big.traffic.authcenter.Page;
import com.dahua.big.traffic.authcenter.PageRequest;
import com.dahua.big.traffic.bean.BackupConfig;
import com.dahua.big.traffic.bean.FileMapping;
import com.dahua.big.traffic.bean.base.UserRoleInfo;
import com.dahua.big.traffic.configuration.ResourceConfiguration;
import com.dahua.big.traffic.constant.Constant;
import com.dahua.big.traffic.constant.ErrorEnum;
import com.dahua.big.traffic.dal.system.SysBackupMapper;
import com.dahua.big.traffic.enums.BackupType;
import com.dahua.big.traffic.enums.DateEnum;
import com.dahua.big.traffic.init.DataSource;
import com.dahua.big.traffic.service.impl.BaseService;
import com.dahua.big.traffic.utils.CommandUtil;
import com.dahua.big.traffic.utils.DateUtils;
import com.dahua.big.traffic.utils.TokenUtil;
import com.dahua.big.traffic.videoservice.OSCategory;
import com.dahua.big.traffic.videoservice.exception.BasicException;
import com.dahua.big.traffic.videoservice.sys.SysDataBackup;
import com.dahua.big.traffic.videoservice.sys.SysDataRestore;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

*/
/**
 * 备份实现
 *
 * @Version 1.0
 * @Project next-as-api
 * @Title com.dahuatech.next.as.api.modules.sys.backup.service.impl
 * @Date 2019/4/15
 * <p>
 * 手动备份
 * @param request
 * @throws BasicException
 * <p>
 * 自动备份，每天中午12点自动备份
 * <p>
 * 备份输出文件
 * @param fileName
 * @return 生成备份命令，字符集utf-8，insert语句拆为多行，导出所有存储过程和事件
 * @param ip
 * @param database
 * @param tables
 * @return 生成还原命令
 * @param ip
 * @param filePath
 * @param fileName
 * @return 手动备份
 * @param request
 * @throws BasicException
 * <p>
 * 自动备份，每天中午12点自动备份
 * <p>
 * 备份输出文件
 * @param fileName
 * @return 生成备份命令，字符集utf-8，insert语句拆为多行，导出所有存储过程和事件
 * @param ip
 * @param database
 * @param tables
 * @return 生成还原命令
 * @param ip
 * @param filePath
 * @param fileName
 * @return
 *//*


@Service
@Slf4j
public class SysBackupServiceImpl */
/*extends BaseService implements SysBackupService*//*
 {

	@Value("${jdbc.ip:127.0.0.1}")
	String jdbcIp;
	@Value("${jdbc.port:3306}")
	int jdbcPort;
	@Autowired
	private SysBackupMapper sysBackupMapper;
	@Autowired
	DataSource dataSource;
	@Autowired
	ResourceConfiguration resourceConfiguration;

	*/
/**
 * 手动备份
 *
 * @param request
 * @throws BasicException
 *//*

	@Override
	public void doSysDataBackup(HttpServletRequest request) throws BasicException {
		// 查询用户信息
		String token = getToken(request);
		UserRoleInfo userRoleInfo = TokenUtil.getUserRoleInFo(token, authCenterUrl);
		boolean flag = doBackup(userRoleInfo == null ? null : userRoleInfo.getUserCode(), BackupType.MANUAL);
		if (!flag) {
			throw new BasicException(String.valueOf(ErrorEnum.SERVER_EXCEPTION.getHttp_code()),
					"insert SysDataBackup failed ");
		}
	}

	*/
/**
 * 自动备份，每天中午12点自动备份
 *//*

	@Override
	@Scheduled(cron = "0 0 12 * * ?")
	public void autoBackup() {
		if (OSCategory.LINUX.equals(OSCategory.currentOS())) {
			// 失败的时候最多执行三次
			int count = 3;
			while (count > 0) {
				boolean flag = doBackup(Constant.SYSTEM_USER_CODE, BackupType.AUTO);
				if (flag) {
					break;
				}
				// 失败继续备份，重试二次
				count--;
			}
		}
	}

	private boolean doBackup(String userCode, BackupType backupType) {
		String time = DateUtils.getNowDateString(DateEnum.DATE_05.getDate());
		String fileName = backupType.name() + "_" + time + ".sql";
		FileMapping backupFileMapping = resourceConfiguration.build(ResourceConfiguration.backupDir, fileName);
		boolean flag = doSysDataBackupOut2File(backupFileMapping.getLocal());
		if (flag) {
			// 成功插入备份记录
			SysDataBackup sysDataBackup = new SysDataBackup();
			sysDataBackup.setFileName(fileName);
			// 系统管理员
			if (StringUtils.isBlank(userCode)) {
				sysDataBackup.setCreateUser(Constant.SYSTEM_USER_CODE);
			} else {
				sysDataBackup.setCreateUser(userCode);
			}
			sysDataBackup.setBackupType(backupType.ordinal());
			sysBackupMapper.insertSysDataBackupRecord(sysDataBackup);
		}
		return flag;
	}

	@Override
	public void deleteSysDataBackupInfos(String ids) throws BasicException {
		try {
			sysBackupMapper.deleteSysDataBackupInfos(Arrays.asList(ids.split(",")));
		} catch (Exception e) {
			throw new BasicException(String.valueOf(ErrorEnum.SERVER_EXCEPTION.getHttp_code()),
					"delete SysDataBackup failed ");
		}
		sysBackupMapper.deleteSysDataRestoreInfos(Arrays.asList(ids.split(",")));
	}

	@Override
	public Page<SysDataBackup> getSysDataBackupInfos(PageRequest pageRequest) throws BasicException {
		com.github.pagehelper.Page<SysDataBackup> pageHelper = PageHelper.startPage(pageRequest.getPageNumber(),
				pageRequest.getPageSize());
		List<SysDataBackup> backList = sysBackupMapper.getSysDataBackupInfos(pageRequest.getFilters());
		for (SysDataBackup backup : backList) {
			String fileName = backup.getFileName();
			if (StringUtils.isNotBlank(fileName)) {
				String fileUrl = resourceConfiguration.build(ResourceConfiguration.backupDir, fileName).getUrl();
				backup.setFileName(fileUrl);
			}
		}
		Page<SysDataBackup> page = new Page<SysDataBackup>(pageHelper.getPageNum(), pageHelper.getPageSize(),
				(int) pageHelper.getTotal(), backList, pageRequest.getDraw());
		return page;
	}

	@Override
	public List<SysDataBackup> getSysDataBackupInfoByParam(Map<String, Object> param) throws BasicException {
		List<SysDataBackup> result = sysBackupMapper.getSysDataBackupInfos(param);
		for (SysDataBackup backup : result) {
			String fileName = backup.getFileName();
			if (StringUtils.isNotBlank(fileName)) {
				String fileUrl = resourceConfiguration.build(ResourceConfiguration.backupDir, fileName).getUrl();
				backup.setFileName(fileUrl);
			}
		}
		return result;
	}

	@Override
	public void doSysDataRestore(HttpServletRequest request, Map<String, Object> param) throws BasicException {
		// 查询用户信息
		String token = getToken(request);
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
		if (!new File(filePath).exists()) {
			log.error("未找到备份文件:{}", filePath);
			throw new BasicException(String.valueOf(ErrorEnum.SERVER_EXCEPTION.getHttp_code()), "未找到备份文件");
		}
		// 根据文件执行还原
		String shell = getRestoreCommand(filePath);
		log.debug("还原命令:{}", shell);
		String result = CommandUtil.execute(shell);
		if (result != null && result.contains("ERROR")) {
			log.error("还原出错，详细错误信息如下");
			log.error(result);
			return;
		} else {
			SysDataRestore sysDataRestore = new SysDataRestore();
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
			}
		}
	}

	*/
/**
 * 备份输出文件
 *
 * @param fileName
 * @return
 *//*

	public boolean doSysDataBackupOut2File(String filePath) {
		List<BackupConfig> backupConfigs = sysBackupMapper.queryBackupConfig();
		Map<String, List<String>> backupConfigMap = backupConfigs.stream().collect(Collectors.groupingBy(
				BackupConfig::getDatabase, Collectors.mapping(BackupConfig::getTable, Collectors.toList())));
		com.dahua.big.traffic.utils.FileUtils.createIfNotExist(filePath);
		for (Map.Entry<String, List<String>> entry : backupConfigMap.entrySet()) {
			String database = entry.getKey();
			List<String> tables = entry.getValue();
			String shell = getBackupCommandForFile(database, tables, filePath);
			log.debug("nextdb2Tables commandForBackupNextdb2 command is ----------> [{}]", shell);
			try {
				// 只要有一个库备份出错，就报出错
				FileUtils.writeStringToFile(new File(filePath), "use " + database + ";\n\r", true);
				String result = CommandUtil.execute(shell);
				if (result != null && result.contains("ERROR")) {
					log.error("备份出错，详细错误信息如下");
					log.error(result);
					return false;
				}
			} catch (Exception e) {
				log.info("doSysDataBackupOut2File error ----------> [{}]", e);
				return false;
			}
		}
		return true;
	}

	*/
/**
 * 生成备份命令，字符集utf-8，insert语句拆为多行，导出所有存储过程和事件
 *
 * @param ip
 * @param database
 * @param tables
 * @return
 *//*

	private String getBackupCommandForFile(String database, List<String> tables, String filePath) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("mysqldump -h").append(jdbcIp).append(" -P").append(jdbcPort).append(" -u")
				.append(dataSource.getUsername()).append(" -p").append(dataSource.getPassword())
				.append(" --default-character-set=utf8 --skip-extended-insert -R -E ").append(database);
		if (!CollectionUtils.isEmpty(tables))
			stringBuilder.append(" --tables ").append(tables.stream().collect(Collectors.joining(" ")));
		stringBuilder.append(" >> ").append(filePath);
		return stringBuilder.toString();
	}

	*/
/**
 * 生成还原命令
 *
 * @param ip
 * @param filePath
 * @param fileName
 * @return
 *//*

	private String getRestoreCommand(String filePath) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("mysql -h").append(jdbcIp).append(" -P").append(jdbcPort).append(" -u")
				.append(dataSource.getUsername()).append(" -p").append(dataSource.getPassword()).append(" < ")
				.append(filePath);
		return stringBuilder.toString();
	}

}
*/
