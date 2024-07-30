# Bigram_Model_Java_Project
This homework assignment involves creating a basic bigram language model in Java. Here's an overview of what the language model does based on the assignment instructions:

1. Build Vocabulary Index: The model processes a text file to extract a vocabulary of words, limited to 14,500 unique words. Words must contain at least one English letter or be whole numbers. The vocabulary is stored in a String[] array.

2. Build Bigram Counts Array: The model then constructs a 2D array (int[][]) to count the occurrences of word pairs (bigrams) in the text. This array stores how many times each word in the vocabulary is followed by another word in the vocabulary.

3. Save and Load Model: The model can save its vocabulary and bigram counts to two separate files (.voc and .counts). It can also load a previously saved model from these files.

4. Query the Model: The model includes methods to:
    - Retrieve the index of a word in the vocabulary.
    - Get the bigram count for two words.
    - Find the most frequent word that follows a given word.
    - Check if a given sentence is "legal" (all word pairs in the sentence must have been seen together in the training text).
      
5. Cosine Similarity: The model includes a method to calculate the cosine similarity between two vectors, which represent the context of words in the text. This is used to find words with similar contexts.

6. Find Closest Word: Using cosine similarity, the model can find the word in the vocabulary that has the most similar context to a given word.

The implementation relies on handling file I/O, string manipulation, and basic data structures without using generic collections like lists, maps, or sets. This bigram model is a simple statistical representation of language that can be the basis for more advanced natural language processing tasks.
