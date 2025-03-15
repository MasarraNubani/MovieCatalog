package com.example.project4;

public class AVLNode {
    private Movie movie;
    private AVLNode left, right;
    private int height;

    public AVLNode(Movie movie) {
        this.movie = movie;
        this.left = null;
        this.right = null;
        this.height = 1;
    }

    // Getters and Setters
    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public AVLNode getLeft() {
        return left;
    }

    public void setLeft(AVLNode left) {
        this.left = left;
    }

    public AVLNode getRight() {
        return right;
    }

    public void setRight(AVLNode right) {
        this.right = right;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
