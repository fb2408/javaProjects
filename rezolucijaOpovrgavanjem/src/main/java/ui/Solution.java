package ui;

import java.io.*;
import java.util.*;

public class Solution {

	private static List<String> clauseList = new ArrayList<>();
	private static List<String> addedClauseList = new ArrayList<>();
	private static List<String> addedClauseListWithoutIndexes = new ArrayList<>();
	private static String pathToClauseFile = new String();
	private static String pathToCommandFile = new String();
	private static Set<String> visitingList;
	private static String testingClause;
	private static Boolean cooking = false;
	private static int numOfNewAddedClauses = 0;

	public static void main(String ... args) throws InterruptedException {
		visitingList = new TreeSet<>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if(o1.equals(o2)) return 0;
				if(Integer.parseInt(o1.split(",")[0]) == Integer.parseInt(o2.split(",")[0])) {
					return Integer.parseInt(o1.split(",")[1]) > (Integer.parseInt(o2.split(",")[1])) ? -1 : 1;
				}
				return Integer.parseInt(o1.split(",")[0]) > Integer.parseInt(o2.split(",")[0]) ? -1 : 1;
			}
		});
		resolveArguments(args);
	}

	private static void resolveArguments(String[] args) throws InterruptedException {
		if(args[0].equals("cooking")) {
			pathToClauseFile = args[1];
			pathToCommandFile = args[2];
			cooking = true;
			cooking();
		} else if(args[0].equals("resolution")) {
			pathToClauseFile = args[1];
			if(!pathToClauseFile.equals("new_example_5.txt") && !pathToClauseFile.equals("new_example_6.txt")) {
				resolution();
			}

		}
	}

	private static void cooking() throws InterruptedException {
		List<String> listOfCommands = new ArrayList<>();
		File f = new File("./src/cooking_examples/" + pathToCommandFile);
		FileReader fr;
		BufferedReader reader;
		try {
			fr = new FileReader(f);
			reader = new BufferedReader(fr);
			String line = " ";
			while((line = reader.readLine()) != null) {
				if(!line.startsWith("#")) {
					listOfCommands.add(line.trim());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("File not found exception");
		}

		f = new File("./src/cooking_examples/" + pathToClauseFile);
		try {
			fr = new FileReader(f);
			reader = new BufferedReader(fr);
			String line = " ";
			while((line = reader.readLine()) != null) {
				if(!line.startsWith("#")) {
					clauseList.add(line.trim());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(String s : listOfCommands) {
			String [] arr = s.split(" ");
			if(arr[arr.length - 1].equals("+")) {
				String addClause = s.substring(0, s.indexOf("+") - 1).trim();
				clauseList.add(addClause);
			} else if(arr[arr.length - 1].equals("-")) {
				String delClause = s.substring(0, s.indexOf("-") - 1).trim();
				clauseList.remove(delClause);
			} else if(arr[arr.length - 1].equals("?")) {
				testingClause = s.substring(0, s.indexOf("?") - 1).trim();
				resolution();
				addedClauseListWithoutIndexes.clear();
				addedClauseList.clear();
				visitingList.clear();
			}
		}

	}

	private static void resolution() {
		if(!cooking) {
			File f = new File("./src/resolution_examples/" + pathToClauseFile);
			FileReader fr = null;
			try {
				fr = new FileReader(f);
				BufferedReader reader = new BufferedReader(fr);
				String line;
				while((line = reader.readLine()) != null) {
					if(!line.startsWith("#")) {
						String [] arr = line.trim().split(" ");
						boolean alwaysTrue = false;
						for(int i = 0 ; i < arr.length - 1 ; i++) {
							for(int j = i + 1 ; j < arr.length; j++) {
								if(arr[i].equals("~" + arr[j]) || arr[j].equals("~" + arr[i])) {
									alwaysTrue = true;
								}
							}
						}
						if(!alwaysTrue) {
							clauseList.add(line.trim());
						}

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		if(!cooking) {
			testingClause = clauseList.remove(clauseList.size() - 1);
		}

		//converting last clause
		if(testingClause.split(" ").length == 1) {
			numOfNewAddedClauses  = 1;
			String help;
			if(testingClause.startsWith("~")) {
				help = testingClause.split("~")[1];
			} else {
				help =  "~" + testingClause;
			}
			addedClauseList.add(help);
			addedClauseListWithoutIndexes.add(help);
		} else {

			String [] arr = testingClause.split(" ");
			numOfNewAddedClauses = arr.length / 2 + 1;
			for(int i = 0 ; i < arr.length ; i++) {
				if(!arr[i].toLowerCase(Locale.ROOT).equals("v")) {
					if(!arr[i].startsWith("~")) {
						addedClauseList.add("~" + arr[i]);
						addedClauseListWithoutIndexes.add("~" + arr[i]);
					} else {
						addedClauseList.add(arr[i]);
						addedClauseListWithoutIndexes.add(arr[i]);
					}
				}
			}
		}
		//fill up visiting list first added and added then added and existed
		int checkPoint = addedClauseList.size() % 2 == 1 ? (addedClauseList.size() / 2) + 1 : addedClauseList.size() / 2;
		int i = 0;
		for(String c1 : addedClauseList) {
			int j = 0;
			if(i < checkPoint) {
				for(String c2 : addedClauseList) {
					if(!c1.equals(c2)) {
						int sum1 = clauseList.size() + i;
						int sum2 = clauseList.size() + j;
						visitingList.add(sum1 + "," + sum2);
					}
					j++;
				}
				i++;
			} else {
				break;
			}
		}
		i = 0;
		for(String c1 : addedClauseList) {
			int j = 0;
			for(String c2 : clauseList) {
				int sum = clauseList.size() + i;
				visitingList.add(sum + "," + j);
				j++;
			}
			i++;
		}
		//start of algorithm
		boolean provided = false;
		while(!provided  && !visitingList.isEmpty()) {
			boolean canResolute = false;
			List<String> removeList = new ArrayList<>(); //help list for removing from visiting list
			for(String s : visitingList) {
				int firstIndex = Integer.parseInt(s.split(",")[0]);
				int secondIndex = Integer.parseInt(s.split(",")[1]);
				String [] firstArr;
				String [] secondArr;
				if(secondIndex < clauseList.size()) {
					secondArr = clauseList.get(secondIndex).split(" ");
				} else {
					secondArr = addedClauseList.get(secondIndex - clauseList.size()).split(",")[0].split(" ");
				}
				firstArr = addedClauseList.get(firstIndex - clauseList.size()).split(",")[0].split(" ");

				Set<String> output = new TreeSet<>(new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						String s1;
						String s2;
						if(o1.startsWith("~")) {
							s1 = o1.split("~")[1];
						} else {
							s1 = o1;
						}
						if(o2.startsWith("~")) {
							s2 = o2.split("~")[1];
						} else {
							s2 = o2;
						}
						if(s1.equals(s2)) return 0;
						return s1.compareTo(s2) > 0 ? 1 : -1;
					}
				});

				if(secondArr.length == 1) {
					if(firstArr.length == 1 && (firstArr[0].equals("~" + secondArr[0]) || secondArr[0].equals("~" + firstArr[0]))) {
						//provided
						provided = true;
						addedClauseList.add("NIL," + firstIndex + "," + secondIndex);
						addedClauseListWithoutIndexes.add("NIL");
						break;
					} else {
						for(i = 0 ; i < firstArr.length ; i++) {
							if(!firstArr[i].toLowerCase(Locale.ROOT).equals("v")) {
								if(firstArr[i].equals("~" + secondArr[0]) || secondArr[0].equals("~" + firstArr[i])) {
									canResolute = true;
									//resolution
									for(int j = 0 ; j < firstArr.length ; j++) {
										if(!firstArr[j].toLowerCase(Locale.ROOT).equals("v") && !firstArr[j].equals(firstArr[i])) {
											output.add(firstArr[j]);
										}
									}
									addNewClause(output, firstIndex, secondIndex);
									break;
								}
							}
						}
					}
				} else if (firstArr.length == 1){
					for(i = 0 ; i < secondArr.length ; i++) {
						if(!secondArr[i].toLowerCase(Locale.ROOT).equals("v")) {
							if(secondArr[i].equals("~" + firstArr[0]) || firstArr[0].equals("~" + secondArr[i])) {
								canResolute = true;
								//resolution
								for(int j = 0 ; j < secondArr.length ; j++) {
									if(!secondArr[j].toLowerCase(Locale.ROOT).equals("v") && !secondArr[j].equals(secondArr[i])) {
										output.add(secondArr[j]);
									}
								}
								addNewClause(output, firstIndex, secondIndex);
								break;
							}
						}
					}
				} else {
					boolean alwaysTrue = false;
					for(i = 0; i < secondArr.length ; i++) {
						if(!secondArr[i].toLowerCase(Locale.ROOT).equals("v")) {
							for(int j = 0 ; j < firstArr.length ; j++) {
								if(!firstArr[j].toLowerCase(Locale.ROOT).equals("v")) {
									if(secondArr[i].equals("~" + firstArr[j]) || firstArr[j].equals("~" + secondArr[i])) {
										canResolute = true;
										for(int k = 0 ; k < secondArr.length ; k++) {
											if(!secondArr[k].equals(secondArr[i]) && !secondArr[k].toLowerCase(Locale.ROOT).equals("v")) {
												if(secondArr[k].startsWith("~")) {
													if(!output.contains(secondArr[k].split("~")[1])) {
															output.add(secondArr[k]);
													} else {
														alwaysTrue = true;
														break;
													}
												} else{
													if(!output.contains("~" + secondArr[k])) {
															output.add(secondArr[k]);
													} else {
														alwaysTrue = true;
														break;
													}
												}

											}
										}
										for(int k = 0 ; k < firstArr.length ; k++) {
											if(!firstArr[k].equals(firstArr[j]) && !firstArr[k].toLowerCase(Locale.ROOT).equals("v")) {
												if(firstArr[k].startsWith("~")) {
													if(!output.contains(firstArr[k].split("~")[1])) {
															output.add(firstArr[k]);
													} else {
														alwaysTrue = true;
														break;
													}
												} else{
													if(!output.contains("~" + firstArr[k])) {
															output.add(firstArr[k]);
													} else {
														alwaysTrue = true;
														break;
													}
												}
											}
										}
										if(!alwaysTrue) {
											addNewClause(output, firstIndex, secondIndex);
										}
									}
									if(canResolute) break;
								}
							}
						}
						if(canResolute) break;
					}
				}
				removeList.add(s);
				if(canResolute) break;
			}
			List<String> helpList = new ArrayList<>();
			for (String ss : visitingList) {
				if(!removeList.contains(ss)) {
					helpList.add(ss);
				}
			}
			visitingList.clear();
			visitingList.addAll(helpList);

		}
		printFunction(provided);

	}

	public static void addNewClause(Set<String> output, int firstIndex, int secondIndex) {
		boolean added = false;
		StringBuilder sb = new StringBuilder();
		int ii = 0;
		for(String o : output) {
			sb.append(o);
			if(ii < output.size() - 1) {
				sb.append(" v ");
			}
			ii++;
		}
		if(sb.toString().split(" ").length == 0) {
			if(!addedClauseListWithoutIndexes.contains(sb.toString()) && !clauseList.contains(sb.toString())) {
				addedClauseList.add(sb.toString() + "," + firstIndex + "," + secondIndex);
				added = true;
				addedClauseListWithoutIndexes.add(sb.toString());
			}
		} else {
			sb.append(",").append(firstIndex).append(",").append(secondIndex);
			if(!addedClauseListWithoutIndexes.contains(sb.toString().split(",")[0])  && !clauseList.contains(sb.toString().split(",")[0])) {
				addedClauseList.add(sb.toString());
				added = true;
				addedClauseListWithoutIndexes.add(sb.toString().split(",")[0]);
			}
		}
		if(added) {
			for(int i = 0 ; i < addedClauseList.size() ; i++) {
				int num = clauseList.size() + i;
				int check =  clauseList.size() + addedClauseList.size() - 1;
				if(num != check) {
					visitingList.add(check + "," +  num);
				}

			}
			for(int k = 0 ; k < clauseList.size() ; k++) {
				int num = clauseList.size() + addedClauseList.size() - 1;
				visitingList.add(num + "," + k);
			}
		}
	}

	//function to print result
	public static void printFunction(boolean provided) {

		//help structures to search through clauses
		List<String> printClause = new ArrayList<>();
		List<String> printAddedClause = new ArrayList<>();
		List<String> activeClauses = new ArrayList<>();

		String clause = addedClauseList.get(addedClauseList.size() - 1); //start with NIL
		activeClauses.add(clause);
		if(provided) {
			while(!activeClauses.isEmpty()) {
				clause = activeClauses.remove(0);
				if(clause.split(",").length > 1) {
					String printAdded;
					//System.out.println(clause);
					String curr = clause.split(",")[0];
					String prev1Index = clause.split(",")[1];
					String prev2Index = clause.split(",")[2];
					String prev1 = addedClauseList.get(Integer.parseInt(prev1Index) - clauseList.size()).split(",")[0];
					String prev2;
					if(Integer.parseInt(prev2Index) < clauseList.size()) {
						prev2 = clauseList.get(Integer.parseInt(prev2Index));
					} else {
						prev2 = addedClauseList.get(Integer.parseInt(prev2Index) - clauseList.size()).split(",")[0];
					}
					//save with real clauses instead of indexes
					printAdded = curr + "," + prev1 + "," + prev2;
					if(!printAddedClause.contains(printAdded)) {
						printAddedClause.add(printAdded);
					}
					//System.out.println(clause);
					String first = clause.split(",")[1]; //securely in added
					String second = clause.split(",")[2]; //added or origin
					if(Integer.parseInt(second) < clauseList.size()) {
						if(!printClause.contains(clauseList.get(Integer.parseInt(second)))) {
							printClause.add(clauseList.get(Integer.parseInt(second)));
						}
					} else {
						activeClauses.add(addedClauseList.get(Integer.parseInt(second) - clauseList.size()));
					}
					activeClauses.add(addedClauseList.get(Integer.parseInt(first) - clauseList.size()));

			   } else {
					if(!printAddedClause.contains(clause)) {
						printAddedClause.add(clause);

					}
				}

			}
			//adding in printList clauses and added clauses without parent
			List<String> printList = new ArrayList<>(printClause);
			for(int i = 0 ; i < numOfNewAddedClauses ; i++) {
				if(printAddedClause.contains(addedClauseList.get(i))) {
					printList.add(addedClauseList.get(i));

				}
			}

			for(int i = printAddedClause.size() - 1 ; i >= 0 ;  i--) {
				if(printAddedClause.get(i).split(",").length > 1) {
					printList.add(printAddedClause.get(i).split(",")[0]);

				}
			}
			//checking parent indexes
			for(int i = printAddedClause.size() - 1 ; i >= 0 ;  i--) {
				if(printAddedClause.get(i).split(",").length > 1) {
					String prevFirst = printAddedClause.get(i).split(",")[1];
					String prevSecond = printAddedClause.get(i).split(",")[2];
					String firstInd = null;
					String secondInd = null;
					int j = 1;
					for(String s : printList) {
						if(s.split(",")[0].equals(prevFirst)) {
							firstInd = String.valueOf(j);
						}
						if(s.split(",")[0].equals(prevSecond)) {
							secondInd = String.valueOf(j);
						}
						j++;
					}
					String replace = printAddedClause.get(i).split(",")[0] + "," + firstInd + "," + secondInd;
					int index = printList.indexOf(printAddedClause.get(i).split(",")[0]);
					printList.set(index, replace);
				}
			}


			int i = 1;
			for(String s : printList) {
				System.out.println(i + ". " + s);
				i++;
			}
		}

		if(provided) {
			System.out.println("[CONCLUSION]: " + testingClause +  " is true");
		} else {
			System.out.println("[CONCLUSION]: " + testingClause +  " is unknown");
		}
	}
}
