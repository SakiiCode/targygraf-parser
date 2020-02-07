package ml.sakii.targygrafparser;

import java.util.ArrayList;

public class Subject {

	private int semester,credits;
	private String name="",code=null,group="";
	
	public ArrayList<String> course_block_references = new ArrayList<>();
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	private ArrayList<String> prereqs=new ArrayList<>();
	int height=3;
	
	
	
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credits) {
		this.credits = credits;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ArrayList<String> getPrereqs() {
		return prereqs;
	}
	public void setPrereqs(ArrayList<String> prereqs) {
		this.prereqs = prereqs;
	}
}
