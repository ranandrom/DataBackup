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
		String day = formatter.format(now_star.getTime()); // ��ʽ���������
		System.out.println("����ʼʱ��: " + formatter_star.format(now_star.getTime()));
		System.out.println("===============================================");
		System.out.println("DataBackup.1.0.0");
		System.out.println("***********************************************");
		System.out.println();
		
		int args_len = args.length; // ϵͳ�����������Ĳ�������
		String target = null; // ����·��
		String source = null; // ����Դ·��
		
		int logt = 0; // "-t"����������������־
		int logs = 0; // "-s"����������������־
		
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
				System.out.println("�Բ����������Options�����ڣ�����ȱ�������������������²�����ʾ���룡");
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
				System.out.println("�Բ����������Options���ظ�����������²�����ʾ���룡");
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
		emailData = source+"Ŀ¼�����������ѱ�����ɣ�";
		mailTopic = "Ŀ¼�����������ѱ������";
		try {
			SentEmail.sentEmail(mailTopic, emailData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(source+"Ŀ¼�����������ѱ�����ɣ�");
	
		Calendar now_end = Calendar.getInstance();
		SimpleDateFormat formatter_end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println();
		System.out.println("==============================================");
		System.out.println("�������ʱ��: " + formatter_end.format(now_end.getTime()));
		System.out.println();
	}
	
	// ���ѱ��ݵ������ļ�
	public static ArrayList<String> readBackupOver(String filePath)
	{
		ArrayList<String> BackupOver_file_list = new ArrayList<String>(); // ��Ҫ���ݵ��ļ��б�
		try {
			String encoding = "GBK";
			File file = new File(filePath);

			if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); // ���ǵ������ʽ
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;

				while ((lineTxt = bufferedReader.readLine()) != null) {
					BackupOver_file_list.add(lineTxt);
				}
				read.close();
			} else {
				System.out.println();
				System.out.println(filePath + "�ļ�������");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���" + filePath);
			e.printStackTrace();
		}
		return BackupOver_file_list;
	}
	
	// �½�������Ϣ�ļ�
	public static void backupOver(String outputfile, String folder){
		// д������ļ�
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
	
	// �ж�Ŀ¼��С
	public static String folderSize (String folder) {
		String foldersize = null;
		String cmd = "du -sh "+folder;
		String data = null;
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			// ѭ������ϵͳ�������ݣ���֤ϵͳ�����Ѿ���������
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
	 * ����������ʽ�ķ�����
	 * 
	 * @param str
	 * @param regEx
	 * @return String
	 */
	public static String Regular_Expression(String str, String regEx)
	{
		String data = null;
		// ����������ʽ
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			data = matcher.group();
		}
		return data;
	}
	
	// ����Ŀ¼�ķ�����
	public static void my_mkdir(String dir_name)
	{
		File file = new File(dir_name);
		// ����ļ������ڣ��򴴽�
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
			//System.out.println(dir_name);
		}
	}
	
	// ��ȡ�ļ�md5
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
	
	// rsync�ļ�
	public static void rsyncFile(String sourceFolder, String targetFolder){
		String cmd = "rsync -aP --no-links --include='*' --include='*/'  --exclude='*' " + sourceFolder + " " + targetFolder+"/";
		//System.out.println(cmd);
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			String Dir = null;
			ArrayList<String> Dir_file_list = new ArrayList<String>(); // ��Ҫ���ݵ��ļ��б�
			String AllFileMD5List = targetFolder+"/AllFileMD5List/";
			my_mkdir(AllFileMD5List); // �ڱ���Ӳ�̴���Ŀ¼
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
											System.out.println(targetFolder+"/"+Dir_file_list.get(i)+"�ļ�����1000�λ�ȡmd5ʧ�ܣ�");
											String emailData = targetFolder+"/"+Dir_file_list.get(i)+"�ļ�����1000�λ�ȡmd5ʧ�ܣ�";
											String mailTopic = targetFolder+"/"+Dir_file_list.get(i)+"�ļ�����1000�λ�ȡmd5ʧ�ܣ�";
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
										String targetFileSize = folderSize (targetFolder+"/"+Dir_file_list.get(i)); // ��ȡӲ���ļ���С
										addDataInfoToFile(targetfile, folderName, FileName, targetFileSize, targetFileMD5);
									}else{
										System.out.println("Դ�ļ�"+sourceFolder+"/"+Dir_file_list.get(i)+
												"�뱸���ļ�"+targetFolder+"/"+Dir_file_list.get(i)+"md5�ԱȲ�һ�£�����ԭ��");
										
										String emailData = "Դ�ļ�"+sourceFolder+"/"+Dir_file_list.get(i)+
												"�뱸���ļ�"+targetFolder+"/"+Dir_file_list.get(i)+"md5�ԱȲ�һ�£�����ԭ��";
										String mailTopic = "md5�ԱȲ�һ�£�����ԭ��";
										try {
											SentEmail.sentEmail(mailTopic, emailData);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}
							String targetFolderSize = folderSize (targetFolder+"/"+Dir); // ��ȡĿ¼�����ļ��ܴ�С
							addDataInfoToFile(targetfile, "Total size:", targetFolderSize, targetFolder+Dir, "");
							backupOver("./backup-10.2.txt", Dir);
							System.out.println(Dir+" Ŀ¼�������!");
							System.out.println();
							String emailData = Dir+" Ŀ¼�������!";
							String mailTopic = Dir+" Ŀ¼�������!";
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
	
	// �½�������Ϣ�ļ�
	public static void newDataInfoFile(String outputfile){
		int x = 0;
		while (true) {
			// д������ļ�
			try {
				FileWriter fw = new FileWriter(outputfile);
				BufferedWriter bw = new BufferedWriter(fw);
				//bw.write("Total size:" + "\t" + folderSize + "\t" + backupFolder + "\r\n");
				bw.write("Path" + "\t" + "FileName" + "\t" + "Size"+ "\t" + "md5"+ "\r\n");
				bw.close();
				fw.close();
				if (x != 0) {
					System.out.println();
					System.out.println("������Ϣ�ļ��쳣�����������Զ��޸��ɹ��� ");
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
				System.out.println("��"+x+"�δ�����Ϣ�ļ��쳣!");
				return;
			} else {
				System.out.println();
				System.out.println("��"+x+"�δ�����Ϣ�ļ��쳣!");
				continue;
			}	
		}
	}
	
	// ��������Ϣ�ļ����������Ϣ
	public static void addDataInfoToFile(String file, String Folder, String FileName, String FileSize, String md5){
		int x = 0;
		while (true) {
			// д������ļ�
			try {
				FileWriter fw = new FileWriter(file,true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(Folder + "\t" + FileName + "\t" + FileSize + "\t" + md5+ "\r\n");
				bw.close();
				fw.close();
				if (x != 0) {
					System.out.println();
					System.out.println("�����Ϣ�ļ��쳣�����������Զ��޸��ɹ��� ");
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
				System.out.println("��"+x+"�������Ϣ�ļ��쳣!");
				return;
			} else {
				System.out.println();
				System.out.println("��"+x+"�������Ϣ�ļ��쳣!");
				continue;
			}	
		}
	}

}