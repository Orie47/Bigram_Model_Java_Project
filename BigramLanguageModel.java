package il.ac.tau.cs.sw1.ex4;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BigramModel {
	public static final int MAX_VOCABULARY_SIZE = 14500;
	public static final String VOC_FILE_SUFFIX = ".voc";
	public static final String COUNTS_FILE_SUFFIX = ".counts";
	public static final String SOME_NUM = "some_num";
	public static final int ELEMENT_NOT_FOUND = -1;
	
	String[] mVocabulary;
	int[][] mBigramCounts;
	
	// DO NOT CHANGE THIS !!! 
	public void initModel(String fileName) throws IOException{
		mVocabulary = buildVocabularyIndex(fileName);
		mBigramCounts = buildCountsArray(fileName, mVocabulary);
	}
	
	
	
	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public String[] buildVocabularyIndex(String fileName) throws IOException{ // Q 1
		//initializing an array with the max_val for the vocabulary
		String[] vocabulary = new String[MAX_VOCABULARY_SIZE];
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = "";
		int indexToInsert = 0;
		//will only add number to vocabulary if this boolean is false.
		Boolean numberInVocab = false;
		while((line=reader.readLine()) != null) {
			String[] words = line.trim().split("\\s+");
			if (words.length > 0) {
				for (int i=0; i<words.length ;i++) {
					
					if (indexToInsert == MAX_VOCABULARY_SIZE) {
						break;
					}
					String word = words[i];
					if (word.equals("")) continue;
					int legality = isLegal(word);
					if (legality==0) {
						continue;
					}
			
					else if (legality==1) {
						if (inVocabulary(word.toLowerCase(), vocabulary)==false) {
							vocabulary[indexToInsert] = word.toLowerCase();
							indexToInsert++;
						}
					}
					else {
						if (numberInVocab) continue;
						else {
							vocabulary[indexToInsert] = SOME_NUM;
							indexToInsert++;
							numberInVocab = true;
						}
					}
				}
			}
			if (indexToInsert == MAX_VOCABULARY_SIZE) {
				break;
			}
			
		}
		reader.close();
		if (indexToInsert != MAX_VOCABULARY_SIZE) {
			return shortenedVocabulary(vocabulary, indexToInsert);
		}
		return vocabulary;
	}
	
	
	//checks if a word is present already in vocabulary, if not returns false
	private static boolean inVocabulary(String word, String[] vocabulary) {
		int i = 0;
		while(i < vocabulary.length && vocabulary[i]!= null){
			if (word.equals(vocabulary[i])) {
				return true;
			} 
			i++;
		}
		return false;	
	}
	
	
	//returns the classification of the legality of the word, if isn't legal returns 0
	private static int isLegal(String word) {
		if (containsLetter(word)) {
			return 1;
		}
		else if(onlyNumbers(word)) {
			return 2;
		}
		else return 0;
	}
	
	
	private static String convertedStr(String str) {
		if (isLegal(str) == 1) {
			return str.toLowerCase();
		}
		else if (onlyNumbers(str) || str.equals(SOME_NUM)) {
			return SOME_NUM;
		}
		else return "+3"; 
	}
	
	
	//returns true if the word has an English letter in it
	private static boolean containsLetter(String word) {
		for (int i=0; i<word.length(); i++) {
			char ch = word.charAt(i);
            if (Character.isLetter(ch)) {
                return true;
            }
		}
		return false;
	}
	
	
	//checks if the string is only comprised of digits
	private static boolean onlyNumbers(String word) {
		for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (Character.isDigit(ch) == false) {
                return false;
            }
        }
		return true;
	}
	
	//will condense the vocabulary Array if it isn't used to the max
	private static String[] shortenedVocabulary(String[] vocabulary, int numOfIndexes) {
		String[] newVocab = new String[numOfIndexes];
		for (int i=0; i<numOfIndexes; i++) {
			newVocab[i] = vocabulary[i];
		}
		return newVocab;
	}
	
	
	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException{ // Q - 2
		int size = vocabulary.length; 
		int[][] pairsCount = new int[size][size];
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = "";
		while((line = reader.readLine()) != null) {
			String[] words = line.trim().split("\\s+");
			for (int i=0; i<words.length-1; i++) {
				if (inVocabulary(convertedStr(words[i]), vocabulary) && inVocabulary(convertedStr(words[i+1]), vocabulary)) {
					pairsCount[findIndex(convertedStr(words[i]), vocabulary)][findIndex(convertedStr(words[i+1]), vocabulary)]++;
				}
			}
		}
		reader.close();
		return pairsCount;
	}
	
	
	//finds the index of a word in vocabulary
	private static int findIndex(String word, String[] vocabulary) {
		int size = vocabulary.length;
		for (int i = 0; i < size; i++) {
			if (word.equals(vocabulary[i])) return i;
		}
		return -1;
	} 
	
	
	/*
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: fileName is a legal file path
	 */
	public void saveModel(String fileName) throws IOException{ // Q-3
		int size = mVocabulary.length;
		BufferedWriter writerVoc = new BufferedWriter(new FileWriter(fileName+VOC_FILE_SUFFIX));
		writerVoc.write(size + " words");
		for (int i = 0; i< size; i++) {
			writerVoc.write(System.lineSeparator()  + i + "," + mVocabulary[i]);
		}
		writerVoc.close(); 
		
		BufferedWriter writerCounts = new BufferedWriter(new FileWriter(fileName+COUNTS_FILE_SUFFIX));
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (mBigramCounts[i][j] > 0) {
					writerCounts.write(i+","+j+":"+mBigramCounts[i][j]+System.lineSeparator());
				}
			}
		}
		writerCounts.close();
	}
	
	
	
	/*
	 * @pre: fileName is a legal file path
	 */
	public void loadModel(String fileName) throws IOException{ // Q - 4
		BufferedReader readerVoc = new BufferedReader(new FileReader(fileName+VOC_FILE_SUFFIX));
		String line = "";
		line = readerVoc.readLine();
		int vocabSize = extractNumber(line);
		String[] newVocablary = new String[vocabSize];
		
		for (int i = 0; i < vocabSize; i++) {
			String newLine = readerVoc.readLine();
			if (newLine == null) {
		        break; // Exit the loop if there are no more lines to read
		    }
			int digitCount = String.valueOf(i).length();
			String word = newLine.substring(digitCount+1);
			newVocablary[i] = word;
		}
		
		readerVoc.close();
		mVocabulary = newVocablary;
		
		BufferedReader readerCounts = new BufferedReader(new FileReader(fileName+COUNTS_FILE_SUFFIX));
		int[][] newCounts = new int[vocabSize][vocabSize];
		while((line=readerCounts.readLine()) != null) {
			int firstIndex = extractNumber(line);
			int digitCount1 = String.valueOf(firstIndex).length();
			int secondIndex = extractNumber(line.substring(digitCount1+1));
			int digitCount2 = String.valueOf(secondIndex).length();
			int value = extractNumber(line.substring(digitCount1+digitCount2+2));
			newCounts[firstIndex][secondIndex]=value;
		}
		readerCounts.close();
		mBigramCounts = newCounts;
	}
	
	
	//returns an int for the number in the beginning of a string
	private static int extractNumber(String str) {
		int endIndex = 0;
        while (endIndex < str.length() && Character.isDigit(str.charAt(endIndex))) {
            endIndex++;
        }
        // Extract the substring containing only the digits
        String numStr = str.substring(0, endIndex);
        
        // Parse the extracted substring to an integer
        int num = Integer.parseInt(numStr);
        return num;
	}

	
	
	/*
	 * @pre: word is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
	 */
	public int getWordIndex(String word){  // Q - 5
		for (int i = 0; i < mVocabulary.length; i++) {
			if (mVocabulary[i].equals(word)) return i;
		}
		return ELEMENT_NOT_FOUND;
	}
	
	
	/*
	 * @pre: word1, word2 are in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
	 * exist in the vocabulary, $ret = 0
	 */
	public int getBigramCount(String word1, String word2){ //  Q - 6
		if (inVocabulary(word1,mVocabulary)&&inVocabulary(word2,mVocabulary)) {
			return mBigramCounts[this.getWordIndex(word1)][this.getWordIndex(word2)];
		}
		return 0;
	}
	
	
	/*
	 * @pre word in lowercase, and is in mVocabulary
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post $ret = the word with the lowest vocabulary index that appears most fequently after word (if a bigram starting with
	 * word was never seen, $ret will be null
	 */
	public String getMostFrequentProceeding(String word){ //  Q - 7
		int vocabIndex = getWordIndex(word);
		int size = mVocabulary.length;
		int maxI = 0;
		for (int i = 0; i < size ; i++) {
			if (mBigramCounts[vocabIndex][i]>mBigramCounts[vocabIndex][maxI]) {
				maxI = i; 
			}
		}
		if (mBigramCounts[vocabIndex][maxI] == 0) return null;
		return mVocabulary[maxI];
	}
	
	
	/* @pre: sentence is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: each two words in the sentence are are separated with a single space
	 * @post: if sentence is is probable, according to the model, $ret = true, else, $ret = false
	 */
	public boolean isLegalSentence(String sentence){  //  Q - 8
		if (sentence.equals("")) return true;
		String[] words = sentence.trim().split("\\s+");
		int size = words.length;
		if (size == 1) {
			if (inVocabulary(words[0], mVocabulary)) {
				return true;
			}
			else return false;
		}
		for (int i = 0; i < size-1; i++) {
			if(!(inVocabulary(words[i], mVocabulary) && (inVocabulary(words[i+1], mVocabulary)))) {
				return false;
			}
			if (inVocabulary(words[i], mVocabulary) && inVocabulary(words[i+1], mVocabulary)) {
				if(!(mBigramCounts[getWordIndex(words[i])][getWordIndex(words[i+1])] > 0)) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	/*
	 * @pre: arr1.length = arr2.legnth
	 * post if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calcluates CosineSim
	 */
	public static double calcCosineSim(int[] arr1, int[] arr2){ //  Q - 9
		if(onlyZeros(arr1) || onlyZeros(arr2)) return -1;
		int numerator = 0;
		int groupA = 0;
		int groupB = 0;
		for (int i = 0 ; i < arr1.length; i++) {
			numerator += arr1[i]*arr2[i];
			groupA += arr1[i]*arr1[i];
			groupB += arr2[i]*arr2[i];
		}
		double denominator = Math.sqrt(groupA)*Math.sqrt(groupB);
		return numerator/denominator;
	}
	
	
	private static boolean onlyZeros(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != 0) {
				return false;
			}
		}
		return true;
	}

	
	/*
	 * @pre: word is in vocabulary
	 * @pre: the method initModel was called (the language model is initialized), 
	 * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w) among all the
	 * other words in vocabulary
	 */
	public String getClosestWord(String word){ //  Q - 10
		if (mVocabulary.length ==1) return mVocabulary[0];
		int index = getWordIndex(word);
		int size = mVocabulary.length;
		double[] ranks = new double[size];
		for (int i = 0; i < size; i++) {
			ranks[i] = calcCosineSim(mBigramCounts[i], mBigramCounts[index]);
		}
		//find the highest index aside for index we have for word (which will always equal to 1).
		int highestIndex = findIndexOfMaxAsideFromIndex(ranks, index);
		return mVocabulary[highestIndex];
	}
	
	
	private static int findIndexOfMaxAsideFromIndex(double[] arr, int givenIndex) {
        double max = Double.NEGATIVE_INFINITY;
        int maxIndex = -1;
        
        for (int i = 0; i < arr.length; i++) {
            if (i == givenIndex) {
                // Skip the given index
                continue;
            }
            if (arr[i] > max) {
                max = arr[i];
                maxIndex = i;
            }
        }
        
        return maxIndex;
    }
	
}
