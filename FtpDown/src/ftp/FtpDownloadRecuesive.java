package ftp;
import java.io.*;
import java.util.HashSet;

import org.apache.commons.net.ftp.*;

public class FtpDownloadRecuesive {
	HashSet<String> excudeDirSet = null;
	FTPClient ftpClient;
	String currentPath;
	public static final String REMOTE_PATH = "/";
	//¡¨Ω”ftp∑˛ŒÒ∆˜
	public boolean ftpConnect(String url)
	{
		boolean b ;

		ftpClient = new FTPClient();

		try {
			ftpClient.setDataTimeout(60000); 
			ftpClient.setConnectTimeout(60000);
			ftpClient.connect(url);
			b = true;
		}catch(Exception e)
		{
			e.printStackTrace();
			b = false;
		}

		return b;
	}
	//µ«¬Ωftp∑˛ŒÒ∆˜
	public boolean ftpLogin(String username, String password)
	{
		boolean b;
		try {
			boolean loginResult = ftpClient.login(username, password);
			int returnCode = ftpClient.getReplyCode();  
			ftpClient.setControlEncoding("utf-8");
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
			conf.setServerLanguageCode("zh");
			ftpClient.configure(conf);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			if (loginResult && FTPReply.isPositiveCompletion(returnCode)) 
			{
				b = true;
			}else
			{
				b = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			b = false;
		} 
		return b;
	}

	public int download(String remotePath, String localPath) throws IOException {		
		//boolean flag = true;

		int updateFileCount=0;

		// ≥ı ºªØFTPµ±«∞ƒø¬º ∏¸∏ƒFTPƒø¬º
		System.out.println(ftpClient.changeWorkingDirectory(remotePath));

		// µ√µΩFTPµ±«∞ƒø¬ºœ¬À˘”–Œƒº˛
		FTPFile[] ftpFiles = ftpClient.listFiles();

		System.out.println(remotePath+" dir files length="+ftpFiles.length);
		// —≠ª∑±È¿˙
		for (FTPFile ftpFile : ftpFiles) { 	 
			if(excudeDirSet.contains(ftpFile.getName())){
				System.out.println("ƒø¬º " + ftpFile.getName() + " “—œ¬‘ÿ£¨Ã¯π˝£°");
				continue;
			}
			
			System.out.println("∑÷Œˆ£∫"+ftpFile.getName());
			String remoteFilePath=remotePath+"/"+ftpFile.getName();
			String localFilePath=localPath+"/"+ftpFile.getName();

			File localFile=new File(localFilePath);

			if(ftpFile.isDirectory()){
				if(localFile.exists()==false){
					localFile.mkdirs();
				}
				
				updateFileCount+=download(remoteFilePath,localFilePath);
				
			}else{
				if(localFile.exists()){
					long remoteFileTime=ftpFile.getTimestamp().getTimeInMillis();
					long localFileTime=localFile.lastModified();

					if(remoteFileTime>localFileTime){
						localFile.delete();
						downloadSingle(remoteFilePath,localFilePath);
						updateFileCount++;
					}	        			
				}else{
					downloadSingle(remoteFilePath,localFilePath);
					updateFileCount++;
				}
			}
		}
		return updateFileCount;
		//	        return flag;
	}

	/**
	 * œ¬‘ÿµ•∏ˆŒƒº˛.
	 * @param localFile ±æµÿƒø¬º
	 * @param ftpFile FTPƒø¬º
	 * @return trueœ¬‘ÿ≥…π¶, falseœ¬‘ÿ ß∞‹
	 * @throws IOException 
	 */
	private boolean downloadSingle(String remoteFile,String localFile ) throws IOException {
		boolean flag = true;
		// ¥¥Ω® ‰≥ˆ¡˜
		OutputStream outputStream = new FileOutputStream(localFile);
		// œ¬‘ÿµ•∏ˆŒƒº˛
		flag = ftpClient.retrieveFile(remoteFile, outputStream);
		// πÿ±’Œƒº˛¡˜
		outputStream.flush();
		outputStream.close();
		return flag;
	}

	public void ftpDisConnect(){
		if (ftpClient.isConnected())
		{
			try
			{
				ftpClient.logout();
				ftpClient.disconnect();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void exculdeDirs(){
		excudeDirSet = new HashSet<String>();
		for(int count = 1901; count <= 1939; count++){
			String tmp = String.valueOf(count);
			excudeDirSet.add(tmp);
		}
	}
	
	
	public static void main(String arg[]){
		FtpDownloadRecuesive ftp=new FtpDownloadRecuesive();
		
		//≈≈≥˝“—œ¬‘ÿ≥…π¶µƒƒø¬º£¨“≤º¥“—œ¬‘ÿµƒƒø¬º≤ª‘Ÿ÷ÿ∏¥œ¬‘ÿ
		ftp.exculdeDirs();
		
		//connect ftp
		boolean resultFlag=ftp.ftpConnect("ftp.ncdc.noaa.gov");
		if(resultFlag==true){			
			System.out.println("connect ftp success");
		}else{
			System.out.println("connect ftp fail");
		}
		
		//login ftp
		resultFlag=ftp.ftpLogin("anonymous", "tterminator@sina.com");
		if(resultFlag==true){
			System.out.println("login ftp success");
		}else{
			System.out.println("login ftp fail");
		}
		
		//recurve download files
		try {
			int count=ftp.download("/pub/data/noaa", "F:/zhoujw/test");
			System.out.println("download files count="+count);			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
