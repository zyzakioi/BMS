public class vInt extends Value{
    private int value;
    public vInt(int value){
        this.value = value;
    }
    public String toString(){
        return Integer.toString(value);
    }
    public int getValue(){
        return value;
    }
    public void setValue(int value){
        this.value = value;
    }
}
