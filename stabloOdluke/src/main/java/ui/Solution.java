package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.invoke.StringConcatFactory;
import java.util.*;

public class Solution {

	public static void main(String ... args) {
		ID3Algorithm model = new ID3Algorithm();
	}

}

class Nodes {

	private String backFeature; //svojstvo po kojem se doslo do toga
	private List<String> listOfSubtrees;

	public Nodes(String backFeature, List<String> listOfSubtrees) {
		this.backFeature = backFeature;
		this.listOfSubtrees = listOfSubtrees;
	}

	public String getBackFeature() {
		return backFeature;
	}

	public List<String> getListOfSubtrees() {
		return listOfSubtrees;
	}

	public void setBackFeature(String backFeature) {
		this.backFeature = backFeature;
	}

	public void setListOfSubtrees(List<String> listOfSubtrees) {
		this.listOfSubtrees = listOfSubtrees;
	}

}

class ID3Algorithm {
	private final List<String> features;
	private List<String> classValues;
	private final List<String> fitRows;
	private final List<String> testRows;
	private Set<Nodes> evidenceTree;

	public ID3Algorithm() {
		features = new ArrayList<>();
		fitRows = new ArrayList<>();
		testRows = new ArrayList<>();
		classValues = new ArrayList<>();
	}
	public void fit(String train_dataset_file){
		File f = new File("./src/files/" + train_dataset_file);
		Scanner sc = null;
		try {
			sc = new Scanner(f);
			String line = "";
			boolean isHeader = true;
			while(sc.hasNextLine() && !(line = sc.nextLine()).isEmpty()) {
				if(isHeader) {
					String headerLine = line.trim();
					String [] headerArr = headerLine.split(",");
					for(int i = 0 ; i < headerArr.length ; i++) {
						if(i != headerArr.length - 1) {
							features.add(headerArr[i]);
						} else {
							if(classValues.contains(headerArr[i])) {
								classValues.add(headerArr[i]);
							}
						}
					}
					isHeader = false;
				} else {
					fitRows.add(line.trim());
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String result = id3(fitRows, fitRows, features, classValues);

	}

	private String id3(List<String> childFitRows, List<String> fitRowsParent, List<String> features, List<String> classValues) {
		String subtrees ="";
		if(childFitRows.size() <= 0) {
			List<String> values = new ArrayList<>();
			for(String s : fitRowsParent) {
				String [] arr =s.split(",");
				values.add(arr[arr.length - 1]);
			}
			String argMax = classValues.get(0);
			int argMaxValue = 0;
			for(String s : values) {
				if(s.equals(argMax)) {
					argMaxValue++;
				}
			}
			for(int i = 1 ; i < classValues.size() ; i++) {
				int currValue = 0;
				for(String s : values) {
					if(s.equals(classValues.get(i))) {
						currValue++;
					}
				}
				if(currValue > argMaxValue) {
					argMax = classValues.get(i);
				}
			}
			return argMax;
		}
		List<String> valuesForCurrent = new ArrayList<>();
		for(String s : childFitRows) {
			String [] arr =s.split(",");
			valuesForCurrent.add(arr[arr.length - 1]);
		}
		String argMaxForCurrent = classValues.get(0);
		int argMaxValue = 0;
		for(String s : valuesForCurrent) {
			if(s.equals(argMaxForCurrent)) {
				argMaxValue++;
			}
		}
		for(int i = 1 ; i < classValues.size() ; i++) {
			int currValue = 0;
			for(String s : valuesForCurrent) {
				if(s.equals(classValues.get(i))) {
					currValue++;
				}
			}
			if(currValue > argMaxValue) {
				argMaxForCurrent = classValues.get(i);
			}
		}

		boolean checkEquality = true;
		List<String> deletedByArgMax = new ArrayList<>();
		for(String s : childFitRows) {
			int checkColumn = classValues.indexOf(argMaxForCurrent);
			if(!s.split(",")[checkColumn].equals(argMaxForCurrent)) {
				deletedByArgMax.add(s);
			}
		}
		for(int i  = 0 ; i < childFitRows.size() ; i++) {
			if(!childFitRows.get(i).equals(deletedByArgMax.get(i)))  {
				checkEquality = false;
				break;
			}
		}

		if(features.size() <= 0 || checkEquality) {
			return argMaxForCurrent;
		}
		String feature = resolveMostDiscriminate(childFitRows, features);
		List<String> valuesOfFeature= new ArrayList<>();
		for(String s : childFitRows) {
			if(!valuesOfFeature.contains(s.split(",")[features.indexOf(feature)])) {
				valuesOfFeature.add(s.split(",")[features.indexOf(feature)]);
			}
		}
		for(String s : valuesOfFeature) {
			String t = id3();
			subtrees +=
		}

	}

	private String resolveMostDiscriminate(List<String> currentFitRows, List<String> currFeatures) {
		List<Integer> IDs = new ArrayList<>();
		String result = "";

		int index = 0;
		for(String feature : currFeatures) {
			int ID = 0;
			List<Integer> appearances = new ArrayList<>();
			int E = 0;
			for(int i = 0 ; i < classValues.size() ; i++) {
				int curr = 0;
				for(String s : currentFitRows) {
					String [] arr = s.split(",");
					if(arr[arr.length - 1].equals(classValues.get(i))) {
						curr++;
					}
				}
				appearances.add(curr);
			}
			for(Integer i : appearances) {
				E -= ((float) i /fitRows.size()) * (Math.log((float) i /fitRows.size())/Math.log(2));
			}
			List<String> valuesOfCurrentFeature = new ArrayList<>();
			for(String s : currentFitRows) {
				if(!valuesOfCurrentFeature.contains(s.split(",")[index])) {
					valuesOfCurrentFeature.add(s.split(",")[index]);
				}
			}

			int sum = 0;
			for(String s : valuesOfCurrentFeature) {
				List<String> filteredByCurentFeature = new ArrayList<>();
				for(String ss : currentFitRows) {
					if(ss.split(",")[index].equals(s)) {
						filteredByCurentFeature.add(ss);
					}
				}
				List<Integer> appearances2 = new ArrayList<>();
				for(String ss2 : classValues) {
					int curr = 0;
					for(String ss3 : filteredByCurentFeature) {
						if(ss3.split(",")[features.size()].equals(ss2)) {
							curr++;
						}
					}
					appearances2.add(curr);
				}
				int E2 = 0;
				for(Integer i : appearances2) {
					E2-= ((float) i /filteredByCurentFeature.size()) * (Math.log((float) i /filteredByCurentFeature.size())/Math.log(2));
				}
				sum += E2*((float) filteredByCurentFeature.size() * fitRows.size());
			}
			index++;
			ID = E - sum;
			IDs.add(ID);
		}
		int max = IDs.get(0);
		int indexOfMax = 0;
		for(int i = 1; i < IDs.size() ; i++) {
			if(IDs.get(i) > max) {
				max = IDs.get(i);
				indexOfMax = i;
			}
		}
		result = currFeatures.get(indexOfMax);
		return result;

	}

	public void predict(String test_dataset_file){
		File f = new File("./src/files/" + test_dataset_file);
		Scanner sc = null;
		try {
			sc = new Scanner(f);
			String line = "";
			boolean isHeader = true;
			while(sc.hasNextLine() && !(line = sc.nextLine()).isEmpty()) {
				if(isHeader) {
					isHeader = false;
				} else {
					testRows.add(line.trim());
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
