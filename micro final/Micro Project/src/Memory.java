
public class Memory {
    byte[] dataMemory ;

    public Memory(){
        dataMemory = new byte[2048];

    }
    public static void print(Object x){
        System.out.println(x);
    }

    public static void main(String[] args){
        Memory mem = new Memory();
       print("datamem "+mem.dataMemory[0]);

    }

}
