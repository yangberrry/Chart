package SheepBC;

public class Chart_Data {

    public String time;
    public String value;

    public Chart_Data(String time,String value){
        this.time = time;
        this.value = value;
    }

    public Chart_Data(String log){
        String[] list = log.split("]");
        time = list[0].replace("[","");
        value = list[1];
    }
}
