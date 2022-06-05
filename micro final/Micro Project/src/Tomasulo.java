import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class Tomasulo {
    static ArrayList<String> instructions = new ArrayList<>();
	static Hashtable<String,Integer> latencies = new Hashtable<>();
    static AddFunctionalUnit adderUnit;		
	static MulFunctionalUnit multiplierUnit;	
	static LoadStoreUnit lsUnit;
	static CommonDataBus cdb;
	static RegFile regFile;
    static Memory mem;		      
    static int currentInstruction; //FROM INSTRUCTION QUEUE
    static int clock=1;
    static byte readValue;
    static byte writeValue;

	public Tomasulo() throws IOException{
        multiplierUnit = new MulFunctionalUnit();
        adderUnit = new AddFunctionalUnit();
        lsUnit=new LoadStoreUnit();
        cdb=new CommonDataBus();
        regFile=new RegFile();
        mem = new Memory();
        currentInstruction=0;
        readValue=0;
        writeValue=0;
    }
	
	public static String issue() {
		  if(currentInstruction==instructions.size()) {
			  return "ALL INSTRUCTIONS ISSUED";
		  }
		  String s =instructions.get(currentInstruction); //NEEDS TO BE INCREMENTED
		  String[] instArr = s.split(" ");
		  String operation=instArr[0]; //ADD/SUB/.....
		  instArr=instArr[1].split(",");
		  String rd=instArr[0];
		  String rs=instArr[1];
		  String rt="";
		  if(instArr.length>2)
			  rt=instArr[2];
		  
		  boolean add=false;
		  boolean sub=false;
		  boolean mul=false;
		  boolean div=false;
		  boolean load=false;
		  boolean store=false;


		  switch(operation) {
		  	case "ADD" : add=true;break;
		  	case "SUB" : sub=true;break;
		  	case "MUL" : mul=true;break;
		  	case "DIV" : div=true;break;
		  	case "L.D"  : load=true;break;
		  	case "S.D" : store=true;break;
		  }
		  
		  if (add || sub) {
			  for(int i=0;i<adderUnit.RS.length;i++) {
				  if(!adderUnit.RS[i].busy) { //can issue inst
					  adderUnit.RS[i].busy=true;
					  adderUnit.RS[i].operation= operation;
					  for(int j=0; j<regFile.registers.length;j++) {
						 if(regFile.registers[j].name.equals(rs)) {
							 if(regFile.registers[j].Qi.equals("0")) {
								 adderUnit.RS[i].Vj=regFile.registers[j].value;
							 }
							 else {
								 adderUnit.RS[i].Qj=regFile.registers[j].Qi; 
							 }
						 }
						 
						 if(regFile.registers[j].name.equals(rt)) {
							 if(regFile.registers[j].Qi.equals("0")) {
								 adderUnit.RS[i].Vk=regFile.registers[j].value;
							 }
							 else {
								 adderUnit.RS[i].Qk=regFile.registers[j].Qi; 
							 }
						 }
						 
						 if(regFile.registers[j].name.equals(rd)) {
							 regFile.registers[j].Qi=adderUnit.RS[i].name;						 
						 }
						  
					  }
					  
					  if(adderUnit.RS[i].Vj!=-1 && adderUnit.RS[i].Vk!=-1) {
						  adderUnit.RS[i].isReady=true;
					  }
					  
					  if(operation.equals("ADD")) {
				        	adderUnit.RS[i].executionCycles=latencies.get("ADD");
					  }
					  else { //SUB
				           adderUnit.RS[i].executionCycles=latencies.get("SUB");
					  }
					  currentInstruction++;
					  return adderUnit.RS[i].name+"";
				  }
			  }
			  
			  
		  }
		  
		  else if(mul || div) {
			  for(int i=0;i<multiplierUnit.RS.length;i++) {
				  if(!multiplierUnit.RS[i].busy) { //can issue inst
					  multiplierUnit.RS[i].busy=true;
					  multiplierUnit.RS[i].operation= operation;
					  for(int j=0; j<regFile.registers.length;j++) {
						 if(regFile.registers[j].name.equals(rs)) {
							 if(regFile.registers[j].Qi.equals("0")) {
								 multiplierUnit.RS[i].Vj=regFile.registers[j].value;
							 }
							 else {
								 multiplierUnit.RS[i].Qj=regFile.registers[j].Qi; 
							 }
						 }
						 
						 if(regFile.registers[j].name.equals(rt)) {
							 if(regFile.registers[j].Qi.equals("0")) {
								 multiplierUnit.RS[i].Vk=regFile.registers[j].value;
							 }
							 else {
								 multiplierUnit.RS[i].Qk=regFile.registers[j].Qi; 
							 }
						 }
						 
						 if(regFile.registers[j].name.equals(rd)) {
							 regFile.registers[j].Qi=multiplierUnit.RS[i].name;						 
						 }
						  
					  }
					  
					  if(multiplierUnit.RS[i].Vj!=-1 && multiplierUnit.RS[i].Vk!=-1) {
						  multiplierUnit.RS[i].isReady=true;
					  }
					  
					  if(operation.equals("MUL")) {
						  multiplierUnit.RS[i].executionCycles=latencies.get("MUL");
					  }
					  else { //DIV
						  multiplierUnit.RS[i].executionCycles=latencies.get("DIV");
					  }
					  currentInstruction++;
					  return multiplierUnit.RS[i].name+"";
				  }
			  }
		  }
		  
		  else if(load) {
			  for(int i=0;i<lsUnit.loadBuffer.length;i++) {
				  if(!lsUnit.loadBuffer[i].busy) { //can issue inst
					  lsUnit.loadBuffer[i].busy=true;
					  lsUnit.loadBuffer[i].operation= operation;
					  lsUnit.loadBuffer[i].A=Integer.parseInt(rs); //EFFECTIVE ADDRESS
					  for(int j=0; j<regFile.registers.length;j++) {
						 if(regFile.registers[j].name.equals(rd)) {
							 regFile.registers[j].Qi=lsUnit.loadBuffer[i].name;						 
						 }
					  }
					  lsUnit.loadBuffer[i].isReady=true;
				      lsUnit.loadBuffer[i].executionCycles=latencies.get("L.D");
					  currentInstruction++;
					  return lsUnit.loadBuffer[i].name+"";
				  }
			  }
		  }
		  
		  else if(store) {
			  for(int i=0;i<lsUnit.storeBuffer.length;i++) {
				  if(!lsUnit.storeBuffer[i].busy) { //can issue inst
					  lsUnit.storeBuffer[i].busy=true;
					  lsUnit.storeBuffer[i].operation= operation;
					  lsUnit.storeBuffer[i].A=Integer.parseInt(rs); //EFFECTIVE ADDRESS
					  for(int j=0; j<regFile.registers.length;j++) {
						 if(regFile.registers[j].name.equals(rd)) {
							 if(regFile.registers[j].Qi.equals("0")) {
								 lsUnit.storeBuffer[i].Vj=regFile.registers[j].value;
							 }
							 else {
								 lsUnit.storeBuffer[i].Qj=regFile.registers[j].Qi; 
							 }
						 }
					  }
					  
					  if(lsUnit.storeBuffer[i].Vj!=-1 ) {
						  lsUnit.storeBuffer[i].isReady=true;
					  }
					  
			          lsUnit.storeBuffer[i].executionCycles=latencies.get("S.D");
					  currentInstruction++;
					  return lsUnit.storeBuffer[i].name+"";
				  }
			  }
		  }
		  
		  return "";
		  
	}
	
	public static void execute(String rs) {
		  for(int i=0;i<adderUnit.RS.length;i++) {
			  //System.out.println(adderUnit.RS[i].isReady);
			  //System.out.println(adderUnit.RS[i].name.equals(rs));
			  //System.out.println(adderUnit.RS[i].executionCycles);
			  if(adderUnit.RS[i].ready() && (!adderUnit.RS[i].name.equals(rs)) && adderUnit.RS[i].executionCycles>0) { //EXECUTE
				  if(adderUnit.RS[i].operation.equals("ADD")) {
				  adderUnit.RS[i].result = adderUnit.RS[i].Vj + adderUnit.RS[i].Vk;
				  }
				  else {
					  adderUnit.RS[i].result = adderUnit.RS[i].Vj - adderUnit.RS[i].Vk;
				  }
				  
				  adderUnit.RS[i].executionCycles--;  
				  if(adderUnit.RS[i].executionCycles==0) {
					  adderUnit.RS[i].resultReady=true;
					  adderUnit.RS[i].finishTime=clock;
				  }
			  }
		  }
		  
		  for(int i=0;i<multiplierUnit.RS.length;i++) {
			  if(multiplierUnit.RS[i].ready() && (!multiplierUnit.RS[i].name.equals(rs)) && multiplierUnit.RS[i].executionCycles>0) { //EXECUTE
				  if(multiplierUnit.RS[i].operation.equals("MUL")) {
				  multiplierUnit.RS[i].result = multiplierUnit.RS[i].Vj * multiplierUnit.RS[i].Vk;
				  }
				  else {
					  multiplierUnit.RS[i].result = multiplierUnit.RS[i].Vj / multiplierUnit.RS[i].Vk;
				  }
				  
				  multiplierUnit.RS[i].executionCycles--;
				  if(multiplierUnit.RS[i].executionCycles==0) {
					  multiplierUnit.RS[i].resultReady=true;
					  multiplierUnit.RS[i].finishTime=clock;
					  //System.out.println(multiplierUnit.RS[i].finishTime);
				  }
			  }
		  }
		  
		  for(int i=0;i<lsUnit.loadBuffer.length;i++) {
			  if(lsUnit.loadBuffer[i].ready() && (!lsUnit.loadBuffer[i].name.equals(rs)) && lsUnit.loadBuffer[i].executionCycles>0) { //EXECUTE
				  readValue = mem.dataMemory[lsUnit.loadBuffer[i].A];
				  lsUnit.loadBuffer[i].executionCycles--;
				  if(lsUnit.loadBuffer[i].executionCycles==0) {
					  lsUnit.loadBuffer[i].resultReady=true;
					  lsUnit.loadBuffer[i].finishTime=clock;
				  }
			  }
		  }
		  
		  for(int i=0;i<lsUnit.storeBuffer.length;i++) {
			  if(lsUnit.storeBuffer[i].ready() && (!lsUnit.storeBuffer[i].name.equals(rs)) && lsUnit.storeBuffer[i].executionCycles>0) { //EXECUTE
				  writeValue=mem.dataMemory[lsUnit.storeBuffer[i].A];
				  lsUnit.storeBuffer[i].executionCycles--;
				  if(lsUnit.storeBuffer[i].executionCycles==0) {
					  lsUnit.storeBuffer[i].resultReady=true;
					  lsUnit.storeBuffer[i].finishTime=clock;
				  }
			  }
		  }
	}
	
	public static void writeBack() {
		for(int i=0;i<adderUnit.RS.length;i++) {
			  if(adderUnit.RS[i].resultReady && clock>adderUnit.RS[i].finishTime) { 
				  cdb.setResult(adderUnit.RS[i].result);
				  cdb.setSource(adderUnit.RS[i].name);
				  adderUnit.updateReservationStations(cdb);
				  multiplierUnit.updateReservationStations(cdb);
				  lsUnit.updateReservationStations(cdb);
				  regFile.updateRegisterFile(cdb);
				  
				  //CLEAR RESERVATION STATION
				  adderUnit.RS[i].clear();

				  return;
			  }
		  } //END OF ADDER UNIT FOR LOOP
		
		for(int i=0;i<multiplierUnit.RS.length;i++) {
			  if(multiplierUnit.RS[i].resultReady && clock>multiplierUnit.RS[i].finishTime) { 
				  cdb.setResult(multiplierUnit.RS[i].result);
				  cdb.setSource(multiplierUnit.RS[i].name);
				  adderUnit.updateReservationStations(cdb);
				  multiplierUnit.updateReservationStations(cdb);
				  lsUnit.updateReservationStations(cdb);
				  regFile.updateRegisterFile(cdb);
				  
				  //CLEAR RESERVATION STATION
				  multiplierUnit.RS[i].clear();
				  
				  return;
			  }
		  } //END OF MULTIPLIER UNIT FOR LOOP
		
		for(int i=0;i<lsUnit.loadBuffer.length;i++) {
			  if(lsUnit.loadBuffer[i].resultReady && clock>lsUnit.loadBuffer[i].finishTime) { 
				  cdb.setResult(lsUnit.loadBuffer[i].result);
				  cdb.setSource(lsUnit.loadBuffer[i].name);
				  adderUnit.updateReservationStations(cdb);
				  multiplierUnit.updateReservationStations(cdb);
				  lsUnit.updateReservationStations(cdb);
				  regFile.updateRegisterFile(cdb);
				  
				  //CLEAR RESERVATION STATION
				  lsUnit.loadBuffer[i].clear();
				  
				  return;
			  }
		  } //END OF LOAD BUFFER FOR LOOP
		
		for(int i=0;i<lsUnit.storeBuffer.length;i++) {
			  if(lsUnit.storeBuffer[i].resultReady && clock>lsUnit.storeBuffer[i].finishTime) { 
				  cdb.setResult(lsUnit.storeBuffer[i].result);
				  cdb.setSource(lsUnit.storeBuffer[i].name);
				  adderUnit.updateReservationStations(cdb);
				  multiplierUnit.updateReservationStations(cdb);
				  lsUnit.updateReservationStations(cdb);
				  regFile.updateRegisterFile(cdb);
				  
				  //CLEAR RESERVATION STATION
				  lsUnit.storeBuffer[i].clear();
				  
				  return;
			  }
		  } //END OF STORE BUFFER FOR LOOP
	}
	
	
	public static void run() {
		String issuedRS="";
		while(!(adderUnit.isFinished() && multiplierUnit.isFinished() && lsUnit.loadFinished() && lsUnit.storeFinished()) ||
				clock==1) {
			System.out.println("CLOCK CYCLE: "+clock);
			if (clock==1) {
				issue();
			}
			else {
				issuedRS=issue();
				execute(issuedRS);
				writeBack();
			}
			
			System.out.println("ADD RESERVATION STATIONS:");
			adderUnit.print();
		   	System.out.println("******************************************************************************************"); 

			System.out.println("MUL RESERVATION STATIONS:");
			multiplierUnit.print();
		   	System.out.println("******************************************************************************************"); 

			System.out.println("LOAD/STORE BUFFER:");
			lsUnit.print();
		   	System.out.println("******************************************************************************************");
		   	
			System.out.println("REGISTER FILE:");
			regFile.print();
		   	System.out.println("******************************************************************************************");
			//INSTRUCTION QUEUE?????
			
			clock++;
		}
		
	}
	
	public static void addInstructionsToQueue(String programFile) {
        try {
            File myObj = new File("src/"+programFile); 
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                instructions.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
	}
	
	@SuppressWarnings("resource")
	public static void inputLatencies() {
	 Scanner myObj = new Scanner(System.in);
   	 
   	 System.out.println("ENTER LATENCY OF ADD INSTRUCTION"); 
   	 String latencyAdd = myObj.nextLine();  
   	 latencies.put("ADD", Integer.parseInt(latencyAdd));
   	 System.out.println("---------------------------------------"); 

   	 
   	 System.out.println("ENTER LATENCY OF SUB INSTRUCTION"); 
   	 String latencySub = myObj.nextLine();     
   	 latencies.put("SUB", Integer.parseInt(latencySub));
   	 System.out.println("---------------------------------------"); 

   	 System.out.println("ENTER LATENCY OF MUL INSTRUCTION"); 
   	 String latencyMul = myObj.nextLine();     
   	 latencies.put("MUL", Integer.parseInt(latencyMul));
   	 System.out.println("---------------------------------------"); 

   	 System.out.println("ENTER LATENCY OF DIV INSTRUCTION"); 
   	 String latencyDiv = myObj.nextLine();     
   	 latencies.put("DIV", Integer.parseInt(latencyDiv));
   	 System.out.println("---------------------------------------"); 

   	 System.out.println("ENTER LATENCY OF LOAD INSTRUCTION"); 
   	 String latencyLD = myObj.nextLine();     
   	 latencies.put("L.D", Integer.parseInt(latencyLD));
   	 System.out.println("---------------------------------------"); 

   	 System.out.println("ENTER LATENCY OF STORE INSTRUCTION"); 
   	 String latencySt = myObj.nextLine();     
   	 latencies.put("S.D", Integer.parseInt(latencySt));
   	 System.out.println("---------------------------------------"); 

	}
	
	public static void main(String args[]) {
    	addInstructionsToQueue("program.txt");
    	inputLatencies();
    	try {
			new Tomasulo();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	run();
    }
}
