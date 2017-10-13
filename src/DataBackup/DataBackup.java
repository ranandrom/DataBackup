package DataBackup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import SentEmail.SentEmail;

public class DataBackup
{
	public static void  main(String[] args) throws InterruptedException{
		System.out.println();
		Calendar now_star = Calendar.getInstance();
		SimpleDateFormat formatter_star = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String day = formatter.format(now_star.getTime()); // 格式化后的日期
		System.out.println("程序开始时间: " + formatter_star.format(now_star.getTime()));
		System.out.println("===============================================");
		System.out.println("DataBackup.1.0.0");
		System.out.println("***********************************************");
		System.out.println();
		
		int args_len = args.length; // 系统传入主函数的参数长度
		String target = null; // 磁盘路径
		String source = null; // 备份源路径
		
		int logt = 0; // "-t"参数输入次数计算标志
		int logs = 0; // "-s"参数输入次数计算标志
		
		for (int len = 0; len < args_len; len += 2) {
			if (args[len].equals("-T") || args[len].equals("-t")) {
				target = args[len + 1];
				logt++;
			} else if (args[len].equals("-S") || args[len].equals("-s")) {
				source = args[len + 1];
				logs++;
			} else if ((args_len == 1) && args[0].equals("-help")) {
				System.out.println();
				System.out.println("Version: V1.0.0");
				System.out.println();
				System.out.println("Usage:\t java -jar DataBackup.jar [Options] [args...]");
				System.out.println();
				System.out.println("Options:");
				System.out.println("-help\t\t Obtain parameter description.");
				System.out.println(
						"-T or -t\t Set target path. The default value is null.");
				System.out.println(
						"-S or -s\t Set source path. The default value is null.");
				System.out.println(
						"-N or -n\t Set hard disk number. The default value is null.");
				System.out.println();
				return;
			} else {
				System.out.println();
				System.out.println("对不起，您输入的Options不存在，或者缺少所需参数，请参照以下参数提示输入！");
				System.out.println();
				System.out.println("Options:");
				System.out.println("-help\t\t Obtain parameter description.");
				System.out.println(
						"-T or -t\t Set target path. The default value is null.");
				System.out.println(
						"-S or -s\t Set source path. The default value is null.");
				System.out.println(
						"-N or -n\t Set hard disk number. The default value is null.");
				System.out.println();
				return;
			}
			if (logt > 1 || logs > 1) {
				System.out.println();
				System.out.println("对不起，您输的入Options有重复，请参照以下参数提示输入！");
				System.out.println();
				System.out.println("Options:");
				System.out.println("-help\t\t Obtain parameter description.");
				System.out.println(
						"-T or -t\t Set target path. The default value is null.");
				System.out.println(
						"-S or -s\t Set source path. The default value is null.");
				System.out.println(
						"-N or -n\t Set hard disk number. The default value is null.");
				System.out.println();
				return;
			}
		}

		String emailData = null;
		String mailTopic = null;
		
		rsyncFile (source, target);
		emailData = source+"目录下所有数据已备份完成！";
		mailTopic = "目录下所有数据已备份完成";
		try {
			SentEmail.sentEmail(mailTopic, emailData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(source+"目录下所有数据已备份完成！");
	
		Calendar now_end = Calendar.getInstance();
		SimpleDateFormat formatter_end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println();
		System.out.println("==============================================");
		System.out.println("程序结束时间: " + formatter_end.format(now_end.getTime()));
		System.out.println();
	}
	
	// 读已备份的名单文件
	public static ArrayList<String> readBackupOver(String filePath)
	{
		ArrayList<String> BackupOver_file_list = new ArrayList<String>(); // 需要备份的文件列表
		try {
			String encoding = "GBK";
			File file = new File(filePath);

			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); // 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;

				while ((lineTxt = bufferedReader.readLine()) != null) {
					BackupOver_file_list.add(lineTxt);
				}
				read.close();
			} else {
				System.out.println();
				System.out.println(filePath + "文件不存在");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错：" + filePath);
			e.printStackTrace();
		}
		return BackupOver_file_list;
	}
	
	// 新建数据信息文件
	public static void backupOver(String outputfile, String folder){
		// 写到输出文件
		try {
			FileWriter fw = new FileWriter(outputfile,true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(folder+ "\r\n");
			bw.close();
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 判断目录大小
	public static String folderSize (String folder) {
		String foldersize = null;
		String cmd = "du -sh "+folder;
		String data = null;
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			// 循环读出系统返回数据，保证系统调用已经正常结束
			while ((line = input.readLine()) != null) {
				data = line;
				//System.out.println(line);
			}
			String dataArr[] = data.split("\t");
			foldersize = dataArr[0];			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return foldersize;
	}
	
	
	/**
	 * 调用正则表达式的方法。
	 * 
	 * @param str
	 * @param regEx
	 * @return String
	 */
	public static String Regular_Expression(String str, String regEx)
	{
		String data = null;
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			data = matcher.group();
		}
		return data;
	}
	
	// 创建目录的方法。
	public static void my_mkdir(String dir_name)
	{
		File file = new File(dir_name);
		// 如果文件不存在，则创建
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
			//System.out.println(dir_name);
		}
	}
	
	// 获取文件md5
	public static String md5sum(String file)
	{
		String cmd = "md5sum " + file;
		String data = null;
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = input.readLine()) != null) {
				String InputArr[] = line.split(" ");
				data = InputArr[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	// rsync文件
	public static void rsyncFile(String sourceFolder, String targetFolder){
		String cmd = "rsync -aP --no-links --include='*' --include='*/'  --exclude='*' " + sourceFolder + " " + targetFolder+"/";
		//System.out.println(cmd);
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			String Dir = null;
			ArrayList<String> Dir_file_list = new ArrayList<String>(); // 需要备份的文件列表
			String AllFileMD5List = targetFolder+"/AllFileMD5List/";
			my_mkdir(AllFileMD5List); // 在备份硬盘创建目录
			while ((line = input.readLine()) != null) {				
				File file = new File(sourceFolder+"/"+line);
				if(file.isFile()){					
					//System.out.println(sourceFile+"/"+line+" isFile!");
					Dir_file_list.add(line);
				}else if(file.isDirectory()){
					String[] DirArr = line.split("/");
					if(DirArr.length==1){
						if(Dir != null){
							String targetfile = AllFileMD5List + Dir + "-backupfile-All-File-Info-10.2.txt";
							if(!Dir_file_list.isEmpty()){
								newDataInfoFile(targetfile);
								for(int i=0; i<Dir_file_list.size(); i++){
									//System.out.println(sourceFile+"/"+Dir_file_list.get(i)+"\t"+md5sum(sourceFile+"/"+Dir_file_list.get(i)));
									//System.out.println(targetFolder+"/"+Dir_file_list.get(i)+"\t"+md5sum(targetFolder+"/"+Dir_file_list.get(i)));
									String sourceFileMD5 = md5sum(sourceFolder+"/"+Dir_file_list.get(i));
									String targetFileMD5 = md5sum(targetFolder+"/"+Dir_file_list.get(i));
									int num = 0;
									while(targetFileMD5==null){
										targetFileMD5 = md5sum(targetFolder+"/"+Dir_file_list.get(i));
										if(num == 1000){
											System.out.println(targetFolder+"/"+Dir_file_list.get(i)+"文件连续1000次获取md5失败！");
											String emailData = targetFolder+"/"+Dir_file_list.get(i)+"文件连续1000次获取md5失败！";
											String mailTopic = targetFolder+"/"+Dir_file_list.get(i)+"文件连续1000次获取md5失败！";
											try {
												SentEmail.sentEmail(mailTopic, emailData);
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											break;
										}
										num++;
									}
									//System.out.println(sourceFolder+"/"+Dir_file_list.get(i)+"\t"+sourceFileMD5);
									//System.out.println(targetFolder+"/"+Dir_file_list.get(i)+"\t"+targetFileMD5);
									if(sourceFileMD5.equals(targetFileMD5)){
										String FileName = new File(targetFolder+"/"+Dir_file_list.get(i)).getName();
										String folderName = new File(targetFolder+"/"+Dir_file_list.get(i)).getParent();
										String targetFileSize = folderSize (targetFolder+"/"+Dir_file_list.get(i)); // 获取硬盘文件大小
										addDataInfoToFile(targetfile, folderName, FileName, targetFileSize, targetFileMD5);
									}else{
										System.out.println("源文件"+sourceFolder+"/"+Dir_file_list.get(i)+
												"与备份文件"+targetFolder+"/"+Dir_file_list.get(i)+"md5对比不一致，请检查原因！");
										
										String emailData = "源文件"+sourceFolder+"/"+Dir_file_list.get(i)+
												"与备份文件"+targetFolder+"/"+Dir_file_list.get(i)+"md5对比不一致，请检查原因！";
										String mailTopic = "md5对比不一致，请检查原因！";
										try {
											SentEmail.sentEmail(mailTopic, emailData);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}
							String targetFolderSize = folderSize (targetFolder+"/"+Dir); // 获取目录所有文件总大小
							addDataInfoToFile(targetfile, "Total size:", targetFolderSize, targetFolder+Dir, "");
							backupOver("./backup-10.2.txt", Dir);
							System.out.println(Dir+" 目录拷贝完成!");
							System.out.println();
							String emailData = Dir+" 目录拷贝完成!";
							String mailTopic = Dir+" 目录拷贝完成!";
							try {
								SentEmail.sentEmail(mailTopic, emailData);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Dir_file_list.clear();
						}
						Dir = DirArr[0];
					}
				}
 				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 新建数据信息文件
	public static void newDataInfoFile(String outputfile){
		int x = 0;
		while (true) {
			// 写到输出文件
			try {
				FileWriter fw = new FileWriter(outputfile);
				BufferedWriter bw = new BufferedWriter(fw);
				//bw.write("Total size:" + "\t" + folderSize + "\t" + backupFolder + "\r\n");
				bw.write("Path" + "\t" + "FileName" + "\t" + "Size"+ "\t" + "md5"+ "\r\n");
				bw.close();
				fw.close();
				if (x != 0) {
					System.out.println();
					System.out.println("创建信息文件异常，但程序已自动修复成功！ ");
					x = 0;
				}
				break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				x++;
			}
			if (x == 100) {
				System.out.println();
				System.out.println("第"+x+"次创建信息文件异常!");
				return;
			} else {
				System.out.println();
				System.out.println("第"+x+"次创建信息文件异常!");
				continue;
			}	
		}
	}
	
	// 向数据信息文件添加数据信息
	public static void addDataInfoToFile(String file, String Folder, String FileName, String FileSize, String md5){
		int x = 0;
		while (true) {
			// 写到输出文件
			try {
				FileWriter fw = new FileWriter(file,true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(Folder + "\t" + FileName + "\t" + FileSize + "\t" + md5+ "\r\n");
				bw.close();
				fw.close();
				if (x != 0) {
					System.out.println();
					System.out.println("添加信息文件异常，但程序已自动修复成功！ ");
					x = 0;
				}
				break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				x++;
			}
			if (x == 100) {
				System.out.println();
				System.out.println("第"+x+"次添加信息文件异常!");
				return;
			} else {
				System.out.println();
				System.out.println("第"+x+"次添加信息文件异常!");
				continue;
			}	
		}
	}

}