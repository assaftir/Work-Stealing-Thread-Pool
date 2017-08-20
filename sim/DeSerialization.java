package bgu.spl.a2.sim;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.*;

public class DeSerialization {

	@SuppressWarnings("unchecked")
	public ConcurrentLinkedQueue<Product> deserialzeFinalProducts(String filePath) 
																		throws ClassNotFoundException {
		ConcurrentLinkedQueue<Product> finalProducts = null;
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			finalProducts = (ConcurrentLinkedQueue<Product>) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return finalProducts;
	}

	private static void writeToFile(ConcurrentLinkedQueue<Product> res, String fileName){

		File file = new File(fileName);

		try (FileOutputStream fos = new FileOutputStream(file);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));) {
			for (Product product : res){
				writeToBufferedWriter(bw, product);
				//bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeToBufferedWriter(BufferedWriter bw, Product product)
			throws IOException{
		bw.write("ProductName: " + product.getName() + "  Product Id = " + product.getFinalId());
		bw.newLine();

		bw.write("PartsList {");
		bw.newLine();
		if (!product.getParts().isEmpty()) {
			for (Product part : product.getParts()) {
				writeToBufferedWriter(bw, part);
			}
		}
		bw.write("}");
		bw.newLine();
	}

	public static void compareTextFiles(String file1, String file2){

		try(BufferedReader reader1 = new BufferedReader(new FileReader("file 1 path"));
			BufferedReader reader2 = new BufferedReader(new FileReader("file 2 path"));){

			String line1 = reader1.readLine();
			String line2 = reader2.readLine();
			boolean areEqual = true;
			int lineNum = 1;
			while (line1 != null && line2 != null){						
				if(!line1.equals(line2)){
					areEqual = false;
					break;
				}
				line1 = reader1.readLine();
				line2 = reader2.readLine();
				lineNum++;
			}
			while(line1 != null && line1.isEmpty()){
				line1 = reader1.readLine();
			}
			while(line2 != null && line2.isEmpty()){
				line2 = reader2.readLine();
			}

			if(areEqual){
				System.out.println("Passed!");
			}
			else{
				System.err.println("Error in line " + lineNum);
				System.err.println("File 1: \"" + line1 +"\"");
				System.err.println("File 2: \"" + line2 + "\"");
			}
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		DeSerialization obj = new DeSerialization();
		try{
			ConcurrentLinkedQueue<Product> finalProducts = obj.deserialzeFinalProducts(
					".../result.ser (simulation output file path)");
			writeToFile(finalProducts, "result.txt");
			compareTextFiles("result.txt", "expected.txt");
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
	}

}
