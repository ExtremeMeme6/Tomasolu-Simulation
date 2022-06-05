public class ReservationStation{
    public String name;
    public boolean busy;
    public String operation;
    public double Vj;
    public double Vk;
    public String Qj;
    public String Qk;
    public int A;
    public double result;
    
    public boolean isReady;
    public boolean resultReady; 
	int executionCycles; //number of execution cycles remaining for currently executing instruction
	int finishTime;
	
    public ReservationStation(String name){
        this.name = name;
        busy = false;
        operation = null;
        Vj = Vk = A = -1;
        Qj = Qk = null;
        resultReady = false;
        isReady=false;
        finishTime=0;
    }
    
    public void clear(){
        busy = false;
        operation = null;
        Vj = Vk = A = -1;
        Qj = Qk = null;
        resultReady = false;
		isReady=false;
		finishTime=0;
    }

    public boolean ready(){
        return (busy == true && Qj == null && Qk == null && resultReady == false);
    }

	void print() {
		System.out.print(name+"      "+operation+"      "+Vj+"    "+ Vk +"     "+ Qj+"     "+Qk+"      "+busy);
	}
}

