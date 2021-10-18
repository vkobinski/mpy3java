package utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JavaRunCommand {

    public ArrayList<String> getMetadata(){
        String s = null;
        ArrayList<String> result = new ArrayList<String>();
        File script = new File("script.py");
        
    	ArrayList<File> songs = new ArrayList<File>();
		File directory = new File("music");
		File[] files = directory.listFiles();
		if(files != null) {
			for(File file : files) {
				songs.add(file);
			}
		}
		
        	
        try {
            Process p = Runtime.getRuntime().exec("python " + script.getAbsolutePath() + " " + songs.get(0).toString());
            
            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                result.add(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            
            System.exit(0);
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
        result.remove(1);
        return result;
    }
}