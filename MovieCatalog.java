package com.example.project4;

import java.util.LinkedList;
import java.util.List;


public class MovieCatalog {

    private LinkedList<AVLTree> hashTable;
    private int size;
    private static final int INITIAL_SIZE = 11;

    public MovieCatalog() {
        allocate(INITIAL_SIZE);
    }


    public void allocate(int size) {
        this.size = size;
        hashTable = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            hashTable.add(new AVLTree());
        }
    }


    public int hashFunction(String title) {
        return Math.abs(title.hashCode()) % size;
        //s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
    }

//    public int hashFunction(String title, int st) {
//        int i = 0;
//        int hash = 0;
//        while (title.length()!=i){
//            hash=hash<<5+title.CharAt(i++);
//        }
//        return hash%st;
//    }

//لو اخدت abc مثال رح تنحسب هيك
//    hashCode = s[0]*31^(2) + s[1]*31^(1) + s[2]*31^(0)
//            = 65*31^2 + 66*31^1 + 67*31^0
//            = 65*961 + 66*31 + 67*1
//            = 62465 + 2046 + 67
//            = 64578

    public void put(Movie movie) {
        if (movie == null || movie.getTitle() == null) {
            return;
        }
        int index = hashFunction(movie.getTitle());

        AVLTree tree = getAVLTreeAtIndex(index);
        if (tree != null) {
            tree.insert(movie);
        }

        if (averageHeight() > 3) {
            resize();
        }
    }


    public Movie get(String title) {
        int index = hashFunction(title);
        AVLTree tree = getAVLTreeAtIndex(index);
        if (tree != null) {
            return tree.get(title);
        }
        return null;
    }


    public void erase(String title) {
        int index = hashFunction(title);
        AVLTree tree = getAVLTreeAtIndex(index);
        if (tree != null) {
            tree.delete(title);
        }
    }

    public void saveMoviesToFile(String filename) {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(filename))) {
            for (AVLTree tree : hashTable) {
                List<Movie> movies = tree.getAllMovies();
                for (Movie movie : movies) {
                    writer.write("Title: " + movie.getTitle() + "\n");
                    writer.write("Description: " + movie.getDescription() + "\n");
                    writer.write("Release Year: " + movie.getReleaseYear() + "\n");
                    writer.write("Rating: " + movie.getRating() + "\n\n");
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }


    public void loadMoviesFromFile(String filename) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filename))) {
            String line;
            String title = null, description = null;
            int releaseYear = 0;
            double rating = 0.0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Title:")) {
                    title = line.substring(6).trim();
                } else if (line.startsWith("Description:")) {
                    description = line.substring(12).trim();
                } else if (line.startsWith("Release Year:")) {
                    releaseYear = Integer.parseInt(line.substring(13).trim());
                } else if (line.startsWith("Rating:")) {
                    rating = Double.parseDouble(line.substring(7).trim());
                } else if (line.trim().isEmpty()) {
                    if (title != null && description != null) {
                        Movie movie = new Movie(title, description, releaseYear, rating);
                        put(movie);
                        title = null;
                        description = null;
                        releaseYear = 0;
                        rating = 0.0;
                    }
                }
            }
            if (title != null && description != null) {
                Movie movie = new Movie(title, description, releaseYear, rating);
                put(movie);
            }
        } catch (java.io.IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }


//    public void deallocate() {
//        hashTable.clear();
//        size = 0;
//    }


    public List<Movie> getAllMovies() {
        LinkedList<Movie> allMovies = new LinkedList<>();
        for (int i = 0; i < hashTable.size(); i++) {
            AVLTree tree = hashTable.get(i);
            allMovies.addAll(tree.getAllMovies());
        }
        return allMovies;
    }



    private double averageHeight() {
        int totalHeight = 0;
        for (int i = 0; i < hashTable.size(); i++) {
            AVLTree tree = hashTable.get(i);
            totalHeight += tree.getTreeHeight();
        }
        return (double) totalHeight / size;
    }


    private void resize() {
        int newSize = nextPrime(size * 2);
        LinkedList<AVLTree> oldHashTable = hashTable;
        allocate(newSize);

        for (int i = 0; i < oldHashTable.size(); i++) {
            AVLTree tree = oldHashTable.get(i);
            List<Movie> movies = tree.getAllMovies();

            for (int j = 0; j < movies.size(); j++) {
                Movie movie = movies.get(j);
                put(movie);
            }
        }
    }


    private int nextPrime(int n) {
        while (true) {
            if (isPrime(n)) {
                return n;
            }
            n++;
        }
    }


    private boolean isPrime(int n) {
        if (n <= 1)
            return false;
        if (n <= 3)
            return true;

        if (n % 2 == 0 || n % 3 == 0)
            return false;

        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        }

        return true;
    }


    private AVLTree getAVLTreeAtIndex(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        for (int i = 0; i < size; i++) {
            if (i == index) {
                return hashTable.get(i);
            }
        }
        return null;
    }


    public LinkedList<AVLTree> getHashTable() {
        return hashTable;
    }

    public int getHashTableSize() {
        return size;
    }
}
