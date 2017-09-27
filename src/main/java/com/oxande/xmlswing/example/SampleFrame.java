package com.oxande.xmlswing.example;

public class SampleFrame extends AbstractSampleFrame {
	public static void main(String[] args) {
		SampleFrame appl = new SampleFrame();
		appl.initComponents();
		appl.setVisible(true);
	}
	
	public void exit(){
		System.exit(0);
	}
}
