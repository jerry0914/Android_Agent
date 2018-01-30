package jh.android.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import jh.android.commonlibrary.Logger;
import jh.android.commonlibrary.Logger.Log_Level;

public class FileIO {
	
	public static String CurrentTime(){
		Date now = new Date();
		return new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(now);
	}	
	
	public static void WriteAPI_Result(String path,String result){	
		try{
			Logger.WriteLog("FileIO", "WriteAPI_Result path= "+path + " ,result= "+result,Log_Level.Debug);
			BufferedWriter bw = null;
			try{
				FileWriter fw = new FileWriter(path, false);
		        bw = new BufferedWriter(fw);		        
		        bw.write(result);
		        bw.newLine();
		        bw.flush();
		        bw.close();
		    }catch(IOException e){
		    	Logger.WriteLog("FileIO", "WriteAPI_Result error 1:"+e.toString(),Log_Level.Error,true,true);
		    }
		}
		catch(Exception e){
			Logger.WriteLog("FileIO", "WriteAPI_Result error 2:"+e.toString(),Log_Level.Error,true,true);
		}
	}
	
	public static void InitializeAPIResults(){
		try{
			File fdReply = new File(StaticVariables.ReplyFolder);
				if(fdReply.exists()){
					fdReply.delete();
				}
			fdReply.mkdirs();
			Logger.WriteLog("FileIO", "InitializeAPIResults finished.");
		}
		catch(Exception e){
			Logger.WriteLog("FileIO", " InitializeAPIResults file error : " + e.getMessage(),Log_Level.Error,true,true);
		}
	}
}
