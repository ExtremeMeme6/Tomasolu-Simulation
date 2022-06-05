
public class MulFunctionalUnit {
	ReservationStation [] RS; 
	int numberOfStations=2; //number of reservation stations-STATIC
	
	public MulFunctionalUnit() {
		RS=new ReservationStation[numberOfStations];
		for (int i = 0; i < numberOfStations; i++) {
			this.RS[i] = new ReservationStation("M" + (i+1));
		}

	}
	
	public boolean isFinished(){
		for(int i=0;i<RS.length; i++){
				if(RS[i].busy)
					return false;
		}
		return true;
	}
	
	void updateReservationStations(CommonDataBus cdb){
		for(int i=0;i<RS.length;i++){
			if(RS[i].busy && cdb.getSource().equals(RS[i].Qj)){
				RS[i].Qj = null;
				RS[i].Vj = cdb.getResult();
			}
			if(RS[i].busy && cdb.getSource().equals(RS[i].Qk)){
				RS[i].Qk = null;
				RS[i].Vk = cdb.getResult();
			}
		}
	}
	
	void print() {
		System.out.println("NAME   "+"OPERATION   "+"Vj      "+"Vk       "+"Qj       "+"Qk       " + "Busy    ");
	   	System.out.println("--------------------------------------------------------------"); 
		for(int i=0;i<RS.length;i++){
			RS[i].print();
			System.out.println();
		}
	}
}
