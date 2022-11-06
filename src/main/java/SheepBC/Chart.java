package SheepBC;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Chart {

    String path;

    public Chart(String path){
        this.path = path;
        makeDir(path);
    }

    public ArrayList<String> getDay_Time_List(int year, int month, int date){
        String path = this.path+"/"+year+"/"+month+"/"+date+".log";
        File file = new File(path);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            return new ArrayList<String>();
        }
        ArrayList<String> list = new ArrayList<>();
        for (String s:reader.lines().toList()){
            list.add(s.split("]")[0].replace("[",""));
        }
        return list;
    }

    public String getData(int year, int month, int date,String time){
        String path = this.path+"/"+year+"/"+month+"/"+date+".log";
        File file = new File(path);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            return null;
        }

        return reader.lines().parallel().filter(s->s.startsWith("["+time+"]")).findAny().get();

    }
    public void add(String num){
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        String Dir_Path = localDate.getYear()+"/"+localDate.getMonthValue();

        makeDir(Dir_Path);
        String day = String.valueOf(localDate.getDayOfMonth());
        day =day.length() == 1 ? "0"+day:day;
        Dir_Path += "/"+day+".log";

        try {
            addValue(Dir_Path,localTime.toString(),num);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void addValue(String path,String key,String value) throws IOException {
        path = this.path+"/"+path;
        File file = new File(path);

        if(!file.exists()){
           file.createNewFile();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> list = reader.lines().toList();
        if(list.size() != 0){
            if(list.get(list.size()-1).startsWith("["+key+"]")){
                writer.flush();
                writer.close();
                return;
            }
        }
        writer.newLine();
        writer.write("["+key+"] "+value);
        writer.flush();
        writer.close();

    }

    private void makeDir(String path){
        String dir = this.path+"/"+path;
        if(path.equals(this.path)) dir = path;
        File file = new File(dir);
        file.mkdirs();

    }
    /**
     * @apiNote time = year/month/Day  옳은 예 2001/01/02,틀린 예 2001/1/2
     */
    public ArrayList<Chart_Data> getChart_Data_list(String Start_time,String End_time) {

        String[] Start_list = Start_time.split("/");
        String[] End_list = End_time.split("/");

        int sy = Integer.parseInt(Start_list[0]);
        int ey = Integer.parseInt(End_list[0]);

        String sts = Start_time.replace("/","");
        String ets = End_time.replace("/","");
        int st = Integer.parseInt(sts);
        int et = Integer.parseInt(ets);

        ArrayList<Chart_Data> chart_data_list = new ArrayList<>();

        File year_list =new File(path);
        for(String y:year_list.list()){
            int year = Integer.parseInt(y);
            if(sy < year & year < ey){
                chart_data_list.addAll(getList_if_not_year_equals(y));
            }else if(sy == year || year == ey){
                chart_data_list.addAll(getList_if_sy_is_sm(y,st,et));
            }
        }


        return chart_data_list;

    }

    private ArrayList<Chart_Data> getList_if_not_year_equals(String year){
        File month_File = new File(path+"/"+year);
        ArrayList<Chart_Data> chart_data_list = new ArrayList<>();
        for (String m:month_File.list()){

            File Day_File = new File(path+"/"+year+"/"+m);

            for (String d:Day_File.list()){

                File day = new File(path+"/"+year+"/"+m+"/"+d);
                try {
                    BufferedReader br = new BufferedReader(new FileReader(day));
                    for(String log:br.lines().toList()){
                        if(!log.equals("")){
                            String ymd = year+"/"+m+"/"+d.replace(".log","")+"/";
                            chart_data_list.add(new Chart_Data(ymd+log));
                        }
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }

        }
        return chart_data_list;
    }

    private ArrayList<Chart_Data> getList_if_sy_is_sm(String year,int st,int et){
        File month_list = new File(path+"/"+year);
        ArrayList<Chart_Data> chart_data_list = new ArrayList<>();

        for(String m:month_list.list()){
            File Day_list = new File(path+"/"+year+"/"+m);
            for(String d:Day_list.list()){
                String time = year+m+d.replace(".log","");
                int time_num = Integer.parseInt(time);
                if(st <= time_num && time_num <= et){

                    File day = new File(path+"/"+year+"/"+m+"/"+d);
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(day));
                        for(String log:br.lines().toList()){
                            if(!log.equals("")){
                                String ymd = year+"/"+m+"/"+d.replace(".log","")+"/";
                                chart_data_list.add(new Chart_Data(ymd+log));
                            }
                        }
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }


                }
            }
        }
        return chart_data_list;
    }

    static public ArrayList<String> getDay_LIst(ArrayList<Chart_Data>... lists){
        ArrayList<String> day_list = new ArrayList<>();
        for(ArrayList<Chart_Data> cd_list:lists){
            for(Chart_Data cd:cd_list){
                day_list.add(cd.time);
            }
        }
        day_list.sort(String.CASE_INSENSITIVE_ORDER);
        day_list = (ArrayList<String>) day_list.stream().distinct().collect(Collectors.toList());
        return day_list;
    }


}
