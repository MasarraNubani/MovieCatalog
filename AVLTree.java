package com.example.project4;

import java.util.LinkedList;
import java.util.List;


public class AVLTree {
    private AVLNode root;


    public void insert(Movie movie) {
        root = insert(root, movie);
    }

    private AVLNode insert(AVLNode node, Movie movie) {
        if (node == null) {
            return new AVLNode(movie);
        }

        if (movie.compareTo(node.getMovie()) < 0) {
            node.setLeft(insert(node.getLeft(), movie));
        } else if (movie.compareTo(node.getMovie()) > 0) {
            node.setRight(insert(node.getRight(), movie));
        } else {
            node.setMovie(movie);
            return node;
        }

        node.setHeight(1 + Math.max(height(node.getLeft()), height(node.getRight())));

        int balance = getBalance(node);


        if (balance > 1 && movie.compareTo(node.getLeft().getMovie()) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && movie.compareTo(node.getRight().getMovie()) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && movie.compareTo(node.getLeft().getMovie()) > 0) {
            node.setLeft(leftRotate(node.getLeft()));
            return rightRotate(node);
        }

        if (balance < -1 && movie.compareTo(node.getRight().getMovie()) < 0) {
            node.setRight(rightRotate(node.getRight()));
            return leftRotate(node);
        }

        return node;
    }


    public void delete(String title) {
        root = delete(root, title);
    }

    private AVLNode delete(AVLNode node, String title) {
        if (node == null) {
            return node;
        }

        if (title.compareToIgnoreCase(node.getMovie().getTitle()) < 0) {
            node.setLeft(delete(node.getLeft(), title));
        } else if (title.compareToIgnoreCase(node.getMovie().getTitle()) > 0) {
            node.setRight(delete(node.getRight(), title));
        } else {
            if ((node.getLeft() == null) || (node.getRight() == null)) {
                AVLNode temp = null;
                if (node.getLeft() != null) {
                    temp = node.getLeft();
                } else if (node.getRight() != null) {
                    temp = node.getRight();
                }

                if (temp == null) {
                    temp = node;
                    node = null;
                } else {
                    node = temp;
                }
            } else {
                AVLNode temp = minValueNode(node.getRight());
                node.setMovie(temp.getMovie());
                node.setRight(delete(node.getRight(), temp.getMovie().getTitle()));
            }
        }

        if (node == null) {
            return node;
        }

        node.setHeight(Math.max(height(node.getLeft()), height(node.getRight())) + 1);

        int balance = getBalance(node);


        if (balance > 1 && getBalance(node.getLeft()) >= 0) {
            return rightRotate(node);
        }

        if (balance > 1 && getBalance(node.getLeft()) < 0) {
            node.setLeft(leftRotate(node.getLeft()));
            return rightRotate(node);
        }

        if (balance < -1 && getBalance(node.getRight()) <= 0) {
            return leftRotate(node);
        }

        if (balance < -1 && getBalance(node.getRight()) > 0) {
            node.setRight(rightRotate(node.getRight()));
            return leftRotate(node);
        }

        return node;
    }


    public Movie get(String title) {
        AVLNode node = get(root, title);
        return node != null ? node.getMovie() : null;
    }

    private AVLNode get(AVLNode node, String title) {
        if (node == null) {
            return null;
        }

        if (title.compareToIgnoreCase(node.getMovie().getTitle()) < 0) {
            return get(node.getLeft(), title);
        } else if (title.compareToIgnoreCase(node.getMovie().getTitle()) > 0) {
            return get(node.getRight(), title);
        } else {
            return node;
        }
    }


    public List<Movie> getAllMovies() {
        List<Movie> movies = new LinkedList<>();
        inOrder(root, movies);
        return movies;
    }

    private void inOrder(AVLNode node, List<Movie> movies) {
        if (node != null) {
            inOrder(node.getLeft(), movies);
            movies.add(node.getMovie());
            inOrder(node.getRight(), movies);
        }
    }


    public int height(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return node.getHeight();
    }
    private int getBalance(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return height(node.getLeft()) - height(node.getRight());
    }

    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.getLeft();
        AVLNode T2 = x.getRight();

        x.setRight(y);
        y.setLeft(T2);

        y.setHeight(Math.max(height(y.getLeft()), height(y.getRight())) + 1);
        x.setHeight(Math.max(height(x.getLeft()), height(x.getRight())) + 1);

        return x;
    }

    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.getRight();
        AVLNode T2 = y.getLeft();

        y.setLeft(x);
        x.setRight(T2);

        x.setHeight(Math.max(height(x.getLeft()), height(x.getRight())) + 1);
        y.setHeight(Math.max(height(y.getLeft()), height(y.getRight())) + 1);

        return y;
    }

    private AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;

        while (current.getLeft() != null) {
            current = current.getLeft();
        }

        return current;
    }


    public int getTreeHeight() {
        return height(root);
    }
}
