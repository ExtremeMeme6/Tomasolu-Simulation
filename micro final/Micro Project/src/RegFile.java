public class RegFile {
	FPR[] registers;
	
	public RegFile() {
		registers=new FPR[32];
		for (int i=0;i<registers.length;i++) {
			registers[i]=new FPR();
			registers[i].name="F"+i;
		}
	}
	
	void updateRegisterFile(CommonDataBus cdb){
		for(int i=0;i<registers.length;i++){
			if(cdb.getSource().equals(registers[i].Qi)){
				registers[i].Qi = "0";
				registers[i].value = cdb.getResult();
			}

		}
	}
	
	void print() {
		System.out.println("NAME  "+"Qi  "+"REG VALUE");
		for(int i=0;i<registers.length;i++) {
			System.out.println(registers[i].name+"    "+registers[i].Qi+"     "+registers[i].value);
		}
	}
}
