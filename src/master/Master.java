package master;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import editor.EditorMain;
import network.NetworkManager;
import org.jsoup.Jsoup;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class Master {

    public static List<Variable> variables = new ArrayList<>();
    public static String[] file;
    public static List<String> commandArgs = new ArrayList<>();
    public static Map<String, Integer> jumpLocs = new HashMap<String, Integer>();

    public static void main(String[] args) {
        FlatDarkLaf.install();
        for(int i = 1; i < args.length; i++) {
            commandArgs.add(args[i]);
        }
        if(args.length == 0 || args[0].equals("editor")){
            try {
                EditorMain.Editor(args[1]);
            } catch (IndexOutOfBoundsException e){
                EditorMain.Editor(System.getProperty("user.dir") + "/new.dlang");
            }
        }else{
            File f = new File(args[0]);
            FileInputStream in = null;
            try {
                in = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] data = new byte[(int) f.length()];
            try {
                in.read(data);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            String fileOutput = new String(data);

            file = fileOutput.split("\n");

            Run(file);
        }
    }

    public static void Run(String[] file){
        for(int i = 0; i < file.length; i++){
            if(file[i].startsWith("#")){
                jumpLocs.put(file[i].substring(1), i);
            }
        }

        int i = 0;

        while(true){

            if(i >= file.length){
                return;
            }

            Object[] b = RunLine(file[i].replace("\r", ""), i);


            System.out.println(b[1]);

            if((int)b[0] != 0){
                i = (int) b[0];
            }else{
                i++;
            }

        }
    }

    public static void displayTray(String caption, String message) throws AWTException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        TrayIcon trayIcon = new TrayIcon(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB), "DLANG");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("nupac");
        tray.add(trayIcon);

        trayIcon.displayMessage(caption, message, TrayIcon.MessageType.INFO);
        tray.remove(trayIcon);
    }

    public static Object[] RunLine(String line, int location) {
        String[] split = line.split(" ");
        if(line == ""){
            return new Object[]{0, ""};
        }
        if(line.startsWith("jmp") || line.startsWith("jump")){
            if(jumpLocs.containsKey(split[1])){
                return new Object[]{jumpLocs.get(split[1]), ""};
            }
        }
        if(line.startsWith("var")){
            String[] varSplit = line.split("=");
            String name = varSplit[0].substring(3).replace(" ", "");
            String value = varSplit[1].substring(1);
            if(value.equals(";")){
                Util.CreateVar(name, null, false);
            }else{
                Util.CreateVar(name, value, false);
            }
            return new Object[]{0, ""};
        }
        if(line.startsWith("set")){
            String[] varSplit = line.split("=");
            String name = varSplit[0].substring(3).replace(" ", "");
            String value = varSplit[1].substring(1);
            Util.CreateVar(name, value, true);
            return new Object[]{0, ""};
        }
        if(line.startsWith("print")){
            String toPrint = split[1];
            return new Object[]{0, Util.GetVar(toPrint).replace(";", "")};
        }
        if(line.startsWith("args")){
            int index = Integer.parseInt(Util.GetVar(split[1]));
            String variable = split[2];
            Util.CreateVar(variable, commandArgs.get(index), true);
            return new Object[]{0, ""};
        }
        if(line.startsWith("maths") || line.startsWith("math")){
            String operation = split[1];
            String num1String = Util.GetVar(split[2]);
            String num2String = Util.GetVar(split[3]);
            String outputVar = split[4];

            float num1 = Float.parseFloat(num1String);
            float num2 = Float.parseFloat(num2String);

            float output = 0;

            if(operation.equals("*")){
                output = num1 * num2;
            }else if(operation.equals("/")){
                output = num1 / num2;
            }else if(operation.equals("-")){
                output = num1 - num2;
            }else if(operation.equals("+")){
                output = num1 + num2;
            }
            if(output % 1 == 0){
                Util.CreateVar(outputVar, ((int)output) + "", true);
            }else{
                Util.CreateVar(outputVar, output + "", true);
            }
            return new Object[]{0, ""};
        }
        if(line.startsWith("wait")){
            try {
                Thread.sleep(Integer.parseInt(split[1]));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new Object[]{0, ""};
        }
        if(line.startsWith("loop")){
            String timesString = split[1];
            timesString = Util.GetVar(timesString);
            String loopLines = split[2];

            int loopCount = location + 1;
            int numOfLines = Integer.parseInt(loopLines);
            for(int i = 0; i < Integer.parseInt(timesString); i++){
                for(int u = loopCount; u < numOfLines + loopCount; u++){
                    RunLine(file[u].replace("\r", ""), u);
                }
            }
            return new Object[]{numOfLines, ""};
        }
        if(line.startsWith("string")){
            String operation = split[1];

            if(operation.equals("append")){
                String varToWrite = split[2];
                String temp = "";
                for(int i = 3; i < split.length; i++){
                    temp = temp + Util.GetVar(split[i]).replace("[]", " ");
                }
                Util.CreateVar(varToWrite, temp, true);
            }
            if(operation.equals("replace")){
                String varToWrite = split[2];
                String string1 = Util.GetVar(split[3]);
                String replace = Util.GetVar(split[4]);
                String string2 = Util.GetVar(split[5]);
                Util.CreateVar(varToWrite, string1.replace(replace, string2), true);
            }
            return new Object[]{0, ""};
        }
        if(line.startsWith("check")){
            String con1 = split[1];
            String con2 = split[2];
            String loopLines = split[3];

            con1 = Util.GetVar(con1);
            con2 = Util.GetVar(con2);

            int numOfLines = Integer.parseInt(loopLines);
            if(con1.equals(con2)){
                int loopCount = location + 1;
                for(int u = loopCount; u < numOfLines + loopCount; u++){
                    RunLine(file[u].replace("\r", ""), u);
                }
            }
            return new Object[]{numOfLines, ""};
        }
        if(line.startsWith("file")){
            try{
                String para1 = split[1];
                if(para1.startsWith("write")){
                    String fileName = Util.GetVar(split[2]);
                    String toWrite = Util.GetVar(split[3]);

                    File f = new File(fileName);
                    f.createNewFile();
                    FileWriter rw = new FileWriter(f);
                    rw.write(toWrite.replace(";;", "\n"));
                    rw.close();
                }
                if(para1.startsWith("read")){
                    String fileName = Util.GetVar(split[2]);
                    String varToRead = split[3];

                    File f = new File(fileName);
                    FileInputStream in = new FileInputStream(f);
                    byte[] data = new byte[(int) f.length()];
                    in.read(data);
                    in.close();
                    String result = new String(data);
                    Util.CreateVar(varToRead, result, true);
                }
                if(para1.startsWith("delete")){
                    String fileName = Util.GetVar(split[2]);
                    new File(fileName).delete();
                }
            }catch(IOException e){
                e.printStackTrace();
            }

            return new Object[]{0, ""};
        }
        if(line.startsWith("system")){
            String param1 = split[1];
            if(param1.startsWith("batch")){
                String fileName = Util.GetVar(split[2]);
                try {
                    Runtime.getRuntime().exec("cmd /c " + fileName).waitFor();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
            if(param1.startsWith("notify")){
                String title = Util.GetVar(split[2]);
                String message = Util.GetVar(split[3]);
                try {
                    displayTray(title, message);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
            if(param1.startsWith("popup")){
                String title = Util.GetVar(split[2]);
                String message = Util.GetVar(split[3]);
                JOptionPane.showMessageDialog(EditorMain.frame, message, title, JOptionPane.INFORMATION_MESSAGE);
            }
            if(param1.startsWith("date")){
                String var = split[2];
                Util.CreateVar(var, Calendar.getInstance().getTime().toString(), true);
            }
            return new Object[]{0, ""};
        }
        if(line.startsWith("network")){
            String param1 = split[1];
            if(param1.startsWith("html")){
                String url = Util.GetVar(split[2]);
                String variable = split[3];
                String html = null;
                try {
                    html = Jsoup.connect(url).get().html();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Util.CreateVar(variable, html, true);
            }
            if(param1.startsWith("tcp")){
                String param2 = split[2];
                if(param2.startsWith("connect")){
                    if(split.length == 3){
                        NetworkManager.Connect();
                    }
                    if(split.length == 4){
                        NetworkManager.Connect(split[3]);
                    }
                }
                if(param2.startsWith("host")){
                    NetworkManager.Host();
                }
                if(param2.startsWith("client")){
                    NetworkManager.Client();
                }
                if(param2.startsWith("send")){
                    NetworkManager.Send(Util.GetVar(split[3]));
                }
                if(param2.startsWith("receive")){
                    NetworkManager.Receive(split[3]);
                }
                if(param2.startsWith("disconnect")){
                    NetworkManager.Disconnect();
                }
            }
            return new Object[]{0, ""};
        }
    }

}
