
public class LoadStoreUnit {
	ReservationStation[] loadBuffer; 
	ReservationStation[] storeBuffer;	
	int numberOfStations=2;
	//int executionCycles;
	
	public LoadStoreUnit() {
		loadBuffer=new ReservationStation[numberOfStations];
		storeBuffer=new ReservationStation[numberOfStations];
		for (int i = 0; i < numberOfStations; i++) {
			this.loadBuffer[i] = new ReservationStation("L" + (i+1));
			this.storeBuffer[i] = new ReservationStation("S" + (i+1));
		}
		
	}
	
	public boolean loadFinished(){
		for(int i=0;i<loadBuffer.length; i++){
				if(loadBuffer[i].busy)
					return false;
		}
		return true;
	}
	
	public boolean storeFinished(){
		for(int i=0;i<storeBuffer.length; i++){
				if(storeBuffer[i].busy)
					return false;
		}
		return true;
	}
	
	void updateReservationStations(CommonDataBus cdb){
		for(int i=0;i<loadBuffer.length;i++){
			if(loadBuffer[i].busy && cdb.getSource().equals(loadBuffer[i].Qj)){
				loadBuffer[i].Qj = null;
				loadBuffer[i].Vj = cdb.getResult();
			}
			if(loadBuffer[i].busy && cdb.getSource().equals(loadBuffer[i].Qk)){
				loadBuffer[i].Qk = null;
				loadBuffer[i].Vk = cdb.getResult();
			}
		}
		
		for(int i=0;i<storeBuffer.length;i++){
			if(storeBuffer[i].busy && cdb.getSource().equals(storeBuffer[i].Qj)){
				storeBuffer[i].Qj = null;
				storeBuffer[i].Vj = cdb.getResult();
			}
			if(storeBuffer[i].busy && cdb.getSource().equals(storeBuffer[i].Qk)){
				storeBuffer[i].Qk = null;
				storeBuffer[i].Vk = cdb.getResult();
			}
		}
	}
	
	void print() {
		System.out.println("NAME   "+"OPERATION   "+"Vj      "+"Vk       "+"Qj       "+"Qk       " + "Busy    ");
	   	System.out.println("--------------------------------------------------------------"); 
		for(int i=0;i<loadBuffer.length;i++){
			loadBuffer[i].print();
			System.out.println();
		}
	   	System.out.println("**************************************************************"); 
		for(int i=0;i<storeBuffer.length;i++){
			storeBuffer[i].print();
			System.out.println();
		}
	}
}
