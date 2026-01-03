/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.iwish.controllers;

import com.iwish.models.User;
import com.iwish.network.NetworkService;
import com.iwish.utils.UserSession;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 *
 * @author ITEi
 */
public class SocialController implements javafx.fxml.Initializable{
    @FXML private TextField searchField;
    @FXML private ListView<String> resultsList;
    @FXML private ListView<String> pendingRequestsList;
    @FXML private ListView<String> friendsList;
    


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(UserSession.getInstance().getUser() == null){
            System.out.println("Critical Error: UserSession is null in SocialController!");
            return;
        }
        try{
            loadPendingRequests();
            loadAcceptedFriends();
        }catch (Exception e){
            System.err.println("Failed to initialize Social screen: "+ e.getMessage());
        }
        
    }
    
    @FXML
    private void handleSearch() throws Exception{
        String query = searchField.getText();
        NetworkService.getInstance().sendRequest("SEARCH_USER");
        NetworkService.getInstance().sendRequest(query);
        NetworkService.getInstance().sendRequest(UserSession.getInstance().getUser().getUsername());
        
        List<User> results = (List<User>) NetworkService.getInstance().receiveResponse();
        resultsList.getItems().clear();
        for (User u : results){
            resultsList.getItems().add(u.getUsername());
        }
    }
    
    @FXML
    private void handleAddFriend(){
        //Get the selected username from the ListView
        String selectedUser = resultsList.getSelectionModel().getSelectedItem();
        
        if (selectedUser == null){
            showAlert("Warning", "Please select a user from the list first!");
            return;
        }
        
        try{
            NetworkService network = NetworkService.getInstance();
            
            //Send the command and data
            network.sendRequest("SEND_FRIEND_REQUEST");
            network.sendRequest(selectedUser);
            network.sendRequest(UserSession.getInstance().getUser().getUsername()); //Sender
            
            //Receive the response from the server
            String response = (String) network.receiveResponse();
            
            if ("SUCCESS".equals(response)){
                showAlert("Success", "Friend request sent to " + selectedUser);
            }else{
                showAlert("Error", "Could not send request. Maybe you are already friends?");
            }
        }catch(Exception e){
            e.printStackTrace();
            showAlert("Connection Error", "Failed to communicate with server.");
        }
    }
    
    private void showAlert(String title, String message){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
    //we can call this method in initialize() or by using a refresh button
    //.....................................................................
    private void loadPendingRequests() throws Exception{
        NetworkService.getInstance().sendRequest("GET_PENDING_REQUESTS");
        NetworkService.getInstance().sendRequest(UserSession.getInstance().getUser().getUsername());
        
        List<String> requests = (List<String>) NetworkService.getInstance().receiveResponse();
        pendingRequestsList.getItems().setAll(requests);
    }
    
    @FXML
    private void handleAcceptRequest(){
        String sender = pendingRequestsList.getSelectionModel().getSelectedItem();
        if (sender == null)return;
        
        try{
            NetworkService.getInstance().sendRequest("ACCEPT_FRIEND");
            NetworkService.getInstance().sendRequest(sender);
            NetworkService.getInstance().sendRequest(UserSession.getInstance().getUser().getUsername());
            
            String response = (String) NetworkService.getInstance().receiveResponse();
            if("SUCCESS".equals(response)){
                showAlert("Accepted", "You are now friends with "+sender);
                loadPendingRequests(); //Refresh the list
            }
        }catch(Exception e){e.printStackTrace();}
    }

    
    private void loadAcceptedFriends(){
        try{
            NetworkService.getInstance().sendRequest("GET_ACCEPTED_FRIENDS");
            NetworkService.getInstance().sendRequest(UserSession.getInstance().getUser().getUsername());
            
            List<String> friends = (List<String>) NetworkService.getInstance().receiveResponse();
            friendsList.getItems().setAll(friends);
        }catch(Exception e){
            System.err.println("Error loading friends list");
        }
    }
    
    @FXML
    private void handleDeclineRequest(){
        String sender = pendingRequestsList.getSelectionModel().getSelectedItem();
        if (sender == null) return;
        
        try{
            NetworkService.getInstance().sendRequest("DECLINE_FRIEND");
            NetworkService.getInstance().sendRequest(sender);
            NetworkService.getInstance().sendRequest(UserSession.getInstance().getUser().getUsername());
            
            String response = (String) NetworkService.getInstance().receiveResponse();
            if("SUCCESS".equals(response)){
                loadPendingRequests(); //Refresh the list
                showAlert("Removed", "Request from " + sender + " has been declined.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
}
