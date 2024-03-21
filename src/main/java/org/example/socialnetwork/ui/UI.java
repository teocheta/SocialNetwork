package org.example.socialnetwork.ui;

public interface UI {
    void run();

    /**
     * Prints the menu
     */
    void printMenu();

    /**
     * Prints the users
     */
    void printUsers();

    /**
     * Prints the friendships
     */
    void printFriendships();


    /**
     * Reads the data for a user and adds it
     */
    void addUser();

    /**
     * Adds a friendship between two users
     */
    void addFriendship();

    /**
     * Removes a user and all its friendships
     */
    void removeUser();

    /**
     * Removes a friendship between two users
     */
    void removeFriendship();

    /**
     * Gets the number of communities
     */
    void getNumberOfCommunities();

    /**
     * Gets the most sociable community
     */
    void getMostSociableCommunity();

    void getFriendsFromMonth();
}


