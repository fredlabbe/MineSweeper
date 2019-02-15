package minesweeper;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import static minesweeper.TitleScreen.lostCount;

public class MineSweeper extends JFrame implements KeyListener
{

    protected boolean isCtrlPressed = false;
    protected JButton[][] board;
    protected int[][] mines = new int[8][8];
    protected Random rng = new Random();
    protected int bombCounter = 0;  
    protected int correctBombs = 0;
    protected boolean isLost = false; 
    protected boolean isWon = false; 

    public MineSweeper()
    {
        super("Mine Sweeper Game");
        setSize(600,600);
        
        
        setFocusable(true);
        addKeyListener(this);
        
        Container pane = getContentPane();
        pane.setLayout(new GridLayout(8,8));
        
        board = new JButton[8][8];
        
        int bombs = 0; 

        System.out.println("Answers:\n");
        while(bombs != 10)
        {
            int y = rng.nextInt(8);
            int z = rng.nextInt(8);
            
            if(mines[y][z] != -1)
            {
                mines[y][z] = -1;
                bombs++; 
                System.out.println("in x: " + (z+1) + " In y: " + (y+1));
            }
        }
        
        for (int i = 0; i < 8; i++) 
        {
            for (int j = 0; j < 8; j++) 
            {
                if(mines[i][j] != -1)
                {
                    bombQty(i,j);
                    mines[i][j] = bombCounter;
                    bombCounter = 0;
                }
            }
        }
        
        for (int i = 0; i < 8; i++) 
        {
            for (int j = 0; j < 8; j++) 
            {
                board[i][j] = new JButton();
                
                final int x = i;
                final int y = j; 
                board[i][j].addKeyListener(this);
                board[i][j].addActionListener(new ActionListener() 
                {
                    @Override
                    public void actionPerformed(ActionEvent e) 
                    {

                            
                        if(isCtrlPressed)
                        {       
                            if (board[x][y].getIcon()!=null) {
                                board[x][y].setIcon(null);    
                            } 
                            
                            else{
                            
                                ImageIcon  bomb = new ImageIcon(getClass().getClassLoader().getResource("Flag.png")); 
                                Image img = bomb.getImage();
                                Image resized  = img.getScaledInstance(board[x][y].getWidth() , board[x][y].getHeight(), java.awt.Image.SCALE_SMOOTH);
                     
                                board[x][y].setIcon(new ImageIcon(resized)); 
                                //isFlag = true; 
                                if(mines[x][y] == -1) 
                                    correctBombs++; 
                                
                                if (correctBombs == 10){  
                                isWon = true; 
                                playSound("yay.wav");
                                verifyGame();
                            }
                                return; 
                            }
                            
                            
                        }
                        
                        else{
                        board[x][y].setEnabled(false);
                        
                        if(mines[x][y] == 0)
                        { 
                            board[x][y].setIcon(null);
                            propagate(x,y,board);
                        }
                        
                        if(mines[x][y] != 0 && mines[x][y] != -1)
                        { 
                            board[x][y].setIcon(null);
                            board[x][y].setFont(new Font("Tahoma",Font.BOLD,50));
                            board[x][y].setText(String.valueOf(mines[x][y]));
                        }
                        
                        if(mines[x][y] == -1)
                            {
                                for (int k = 0; k < 8; k++) 
                                {
                                    for (int l = 0; l < 8; l++) 
                                    {
                                            
                                        if(mines[k][l] == -1)
                                        {
                                            ImageIcon  bomb = new ImageIcon(getClass().getClassLoader().getResource("bomb.png")); 
                                            Image img = bomb.getImage();
                                            Image resized  = img.getScaledInstance(board[k][l].getWidth() , board[k][l].getHeight(), java.awt.Image.SCALE_SMOOTH);
                     
                                            board[k][l].setIcon(new ImageIcon(resized));
                                            isLost = true;
                                            playSound("explosion.wav"); 
                                            

                                        }
                                    }
                                } 
                                verifyGame();
                            } 
                        }}
                });
                pane.add(board[i][j]);    
            } 
    }
        setVisible(true); 

    }
    
    public void propagate(int i, int j, JButton[][] b)
    { 
            if (i > 0 && j > 0)
            { //regarde case haut gauche 
                b[i-1][j-1].doClick(0); 
                //b[i-1][j-1].setIcon(null);
            } 
            
            if (j> 0)
            { //regarde case haut milieu
                b[i][j-1].doClick(0); 
                //b[i][j-1].setIcon(null);
            } 
            if (i < 7 && j < 7 && j > 0)
            { //regarde case haut droite
                b[i+1][j-1].doClick(0); 
                 //b[i+1][j-1].setIcon(null);
            } 
            if (i> 0)
            { //regarde case a gauche
                b[i-1][j].doClick(0); 
              //b[i-1][j].setIcon(null);
            }     
            if (i < 7)
            { //regarde case a droite
                b[i+1][j].doClick(0); 
                //b[i+1][j].setIcon(null);
            } 
            if (i > 0 && j > 0 && j < 7)
            { //regarde case bas gauche
                b[i-1][j+1].doClick(0); 
                //b[i-1][j+1].setIcon(null);
            } 
            if (j< 7)
            { //regarde case bas milieu
                b[i][j+1].doClick(0); 
               //b[i][j+1].setIcon(null);
            } 
            if (i < 7 && j< 7)
            { //regarde case bas droite
                b[i+1][j+1].doClick(0); 
                //b[i+1][j+1].setIcon(null);
            }
}
    
    public void bombQty(int i, int j)
    { //verifie si il y a une bombe adjacente a la case, si la case adjacente est out of bounds, entre jamais dans la verification

    if (i > 0 && j > 0 && mines [i-1][j-1] == -1)
    { //regarde case haut gauche 
        //action bomb found 
        bombCounter++;
    } 
    if (j> 0 && mines [i][j-1] == -1)
    { //regarde case haut milieu
        //action bomb found 
        bombCounter++;
    } 
    if (i < 7 && j < 7 && j > 0 && mines [i+1][j-1] == -1)
    { //regarde case haut droite
        //action bomb found
        bombCounter++;
    } 
    if (i> 0  && mines [i-1][j] == -1)
    { //regarde case a gauche
        //action bomb found
        bombCounter++;
    }     
    if (i < 7 && mines [i+1][j] == -1)
    { //regarde case a droite
        //action bomb found
        bombCounter++;
    } 
    if (i > 0 && j > 0 && j < 7 && mines [i-1][j+1] == -1)
    { //regarde case bas gauche
        //action bomb found
        bombCounter++;
    } 
    if (j< 7 && mines [i][j+1] == -1)
    { //regarde case bas milieu
        //action bomb found
        bombCounter++;
    } 
    if (i < 7 && j< 7 && mines [i+1][j+1] == -1)
    { //regarde case bas droite
        //action bomb found
        bombCounter++;
    }
}
    
    public static void main(String[] args) 
    {
        EventQueue.invokeLater(new Runnable() 
        {
            public void run() 
            {
                new MineSweeper(); 
            }
        });
        
    }

    @Override
    public void keyTyped(KeyEvent e) 
    {
        
    }

    @Override
    public void keyPressed(KeyEvent e) 
    {
        isCtrlPressed = e.isControlDown();
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        isCtrlPressed = e.isControlDown();
    } 
    
    public void playSound(String name){ 
        
        try{
        File wavFile = new File(name); 
        AudioInputStream ais = AudioSystem.getAudioInputStream(wavFile); 
        Clip clip = AudioSystem.getClip(); 
        clip.open(ais); 
        clip.start(); 
        }catch (Exception e) {}
    } 
    
    public void verifyGame(){ 
        
        if(isLost == true) { 
            TitleScreen.lostCount++;
            TitleScreen.displayLosses(); 
            disableBoard();                 
        } 
        
        if(isWon == true){ 
            TitleScreen.wonCount++; 
            TitleScreen.displayWins(); 
            disableBoard();
                   
        }
    } 
    
    public void disableBoard(){
    for(int i = 0; i < 8 ; i++){
        for(int j = 0; j < 8 ; j++){
            board[i][j].setEnabled(false);
        }
    }
    }
    
}
