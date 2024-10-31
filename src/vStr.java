public class vStr extends Value{
    private String value;
    public vStr(String value){
        this.value = value;
    }
    public String toString(){
        return "\"" + value + "\"";
    }
    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }
}
