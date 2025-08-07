
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


import game2D.*;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. 

// Student ID: 3024246


@SuppressWarnings("serial")


public class Game extends GameCore 
{
	// Useful game constants
	static int screenWidth = 1000;
	static int screenHeight = 300;

	// Game constants
    float 	lift = 0.005f;
    float	gravity = 0.0001f;
    ////////////////////////////////////
    //TODO REMOVE IF NOT USED
    int gamescreen;
    float scale;

    int prevState = 0;
    /////////////////////////////////////
    float	upJump = -0.12f;
    float	moveSpeed = 0.09f;

    ////////////////////////////////////
    //TODO REMOVE IF NOT USED

    // Game state flags
    boolean jump = false;

    boolean moveRight = false;

    boolean moveLeft = false;

    int levelIndex = 0;

    boolean onGround = false;



/*    boolean damageTaken = false;

    boolean easy;*/

    boolean debug = false;

    private boolean pausestate = false;

    ////////////////////////////////////

    // Game resources

    //TODO REMOVE ONES IF NEEDED
    Image menu, level1, level2, paralax1, paralaxCloudFront, paralaxLevel2, winscreen, deathscreen, hurtVignettelow, hurtVignetteHigh;

    Animation guiAnim;

    Animation buttonStart, buttonStop;

    Animation idle, move, jumping, damaged;

    Animation skull;

    Animation ruby;

    Animation crabWalk;

    Animation thingWalk;

    Animation bossP;

    int moving = 0;

    //
    Animation landing;
    //


    Sprite goldSkull = null;

    Sprite guiStart = null;

    Sprite guiQuit = null;

    ///
    Sprite	player = null;
    ///

    ArrayList<Sprite>   crab = new ArrayList<Sprite>();

    ArrayList<Sprite>   EvilThing = new ArrayList<Sprite>();

    ArrayList<Sprite>   Bigboss = new ArrayList<Sprite>();

    ArrayList<Sprite>   rubyCollectable = new ArrayList<Sprite>();


    ArrayList<Sprite> 	clouds = new ArrayList<Sprite>();
    ArrayList<Tile>		collidedTiles = new ArrayList<Tile>();

    String[] maps = {"map0.txt", "map1.txt", "map2.txt", "map0.txt", "map0.txt"};

    int currentLevel;

    int xPos, yPos;

    int health, rubyCount;


    TileMap tmap = new TileMap();	// Our tile map, note that we load it in init()
    
    long total;         			// The score will be the total time elapsed since a crash

    boolean isJump = false;




    /**
	 * The obligatory main method that creates
     * an instance of our class and starts it running
     * 
     * @param args	The list of parameters this program might use (ignored)
     */
    public static void main(String[] args) {

        Game gct = new Game();
        gct.init();
        // Start in windowed mode with the given screen height and width
        gct.run(false,screenWidth,screenHeight);
    }

    /**
     * Initialise the class, e.g. set up variables, load images,
     * create animations, register event handlers.
     * 
     * This shows you the general principles but you should create specific
     * methods for setting up your game that can be called again when you wish to 
     * restart the game (for example you may only want to load animations once
     * but you could reset the positions of sprites each time you restart the game).
     */
    public void init()
    {

        // Initialise some of the variables
        rubyCount = 0;
        currentLevel = 0;
        //
        scale = 1.2f;
        //
        gamescreen=0;

        debug = false;


/*        setSize(tmap.getPixelWidth()/4, tmap.getPixelHeight());*/
        setSize(700, 400);
        setVisible(true);

        Sprite s;	// Temporary reference to a sprite

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", maps[currentLevel]);

        // Below we load all of the background and paralax backgrounds
        menu = loadImage("maps/ANTONIO.png");
        level1 = loadImage("maps/background.png");
        level2 = loadImage("maps/background.png");
        paralax1 = loadImage("maps/levelOneCloud.png");
        paralaxCloudFront = loadImage("maps/cloudsHiding.png");
        winscreen = loadImage("maps/winner.png");
        deathscreen = loadImage("maps/death.png");
        paralaxLevel2 = loadImage("maps/paraWaves.png");
        hurtVignettelow = loadImage("maps/damageLow.png");
        hurtVignetteHigh = loadImage("maps/damageHigh.png");
/*        setSize(tmap.getPixelWidth()/4, tmap.getPixelHeight());*/
        setVisible(true);


        //Protag Sprite Loads

        //Loading of all the individual sprites

        idle = new Animation();
        idle.loadAnimationFromSheet("images/playerGeneral.png", 24, 1, 60, 0, 3);

        move = new Animation();
        move.loadAnimationFromSheet("images/playerGeneral.png", 24, 1, 60, 4, 9);

        damaged = new Animation();
        damaged.loadAnimationFromSheet("images/playerGeneral.png", 24, 1, 60, 10, 13);

        jumping = new Animation();
        jumping .loadAnimationFromSheet("images/playerGeneral.png", 24, 1, 60, 14, 24);

        //Item Sprite Loads

        skull = new Animation();
        skull.loadAnimationFromSheet("images/goal.png", 8, 1, 80, 0, 8);

        ruby = new Animation();
        ruby.loadAnimationFromSheet("images/ruby.png", 4, 1, 100, 0, 4);

        //Enemy Loads

        crabWalk = new Animation();
        crabWalk.loadAnimationFromSheet("images/crabEnemy.png", 6, 1, 100, 0, 6);

        thingWalk = new Animation();
        thingWalk.loadAnimationFromSheet("images/evilPirate.png", 6, 1, 60, 0, 6);

        bossP = new Animation();
        bossP.loadAnimationFromSheet("images/bigBaddieBossBIG.png",6,1,10,0,6);

        // Initialise the player with an animation
/*        buttonStart = new Animation();
        buttonStart.loadAnimationFromSheet("images/Play.png", 1, 1, 50);
        guiStart = new Sprite(buttonStart);
        guiStart.show();
        buttonStop = new Animation();
        buttonStop.loadAnimationFromSheet("images/Exit.png", 1, 1, 50);
        guiQuit = new Sprite(buttonStop);
        guiQuit.show();*/

        player = new Sprite(idle);
        player.show();


        //Gold Skull is the goal sprite

        goldSkull = new Sprite(skull);

        
        // Load a single cloud animation
        Animation ca = new Animation();
        ca.addFrame(loadImage("images/cloud.png"), 1000);
        
        // Create 3 clouds at random positions off the screen
        // to the right
        if (currentLevel == 1){
            for (int c=0; c<3; c++)
            {
                s = new Sprite(ca);
                s.setX(screenWidth + (int)(Math.random()*200.0f));
                s.setY(30 + (int)(Math.random()*150.0f));
                s.setVelocityX(-0.02f);
                s.show();
                clouds.add(s);
            }
        }

        //Adding all of the individual repeated sprites into arraylists which will then load copies of each sprite into preset positions

        for (int i = 0; i < 4; i++) {
            s = new Sprite(ruby);
            rubyCollectable.add(s);
            s.show();
        }
        for (int i = 0; i < 4; i++) {
            s = new Sprite(crabWalk);
            crab.add(s);
            s.show();
        }

        for (int i = 0; i < 1; i++) {
            s = new Sprite(bossP);
            Bigboss.add(s);
            s.hide();
        }
        for (int i = 0; i < 5; i++) {
            s = new Sprite(thingWalk);
            EvilThing.add(s);
            s.hide();
        }




        initialiseGame();
      		
        System.out.println(tmap);
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it when restarting
     * the game when the player loses.
     */
    public void initialiseGame()
    {

        //Load tilemaps, score and background music
        tmap.loadMap("maps", maps[currentLevel]);
    	total = 0;
        Sound backgroundMusic = new Sound("sounds/background2.mid");
        Sound backgroundMusicTwo = new Sound("sounds/background1.mid");

        //Only start the background music on level 1
        if (currentLevel == 1){
            backgroundMusic.start();
        }

 /*        backgroundMusic.start();*/
        health = 4;
        xPos = tmap.getTileWidth();
        yPos = tmap.getTileHeight();

        if (currentLevel == 0 || currentLevel == 3){
            //This sets the player positioning on the first screen and the final screen (although player is invisible on the final screens)
            System.out.println(tmap);
            player.setPosition(73, 185);
            System.out.println("Set Player");
            player.setVelocity(0,0);
            System.out.println("Set Velocity");
            player.setAnimation(idle);
            player.show();
            System.out.println("Player Should be Showing");

            //Sets the goal sprite to show
            goldSkull.show();
        }
    	if (currentLevel == 1 || currentLevel == 2){

            System.out.println(tmap);


            System.out.println("Enemies and Collectables Initialised");

            //Player Position Set
            player.setPosition(225, 248);
            System.out.println("Set Player");
            player.setVelocity(0,0);
            System.out.println("Set Velocity");
            player.setAnimation(idle);
            player.show();
            System.out.println("Player Should be Showing");

            goldSkull.show();
            pausestate = false;
            levelIndex = 0;


            //The following code is repeated across the board for all sprites stored in Arraylists.
            //Below the positions of the sprites are stored in arrays. The format for the name corresponds to the
            //sprite (ruby) the level (1) and the axis (x)
            int ruby1x[] = {709, 1722, 2277, 4020};
            int ruby1y[] = {186, 312, 56, 217};
            int ruby2x[] = {161, 2362, 1866, 3196};
            int ruby2y[] = {89, 217, 345, 136};
            int index = 0;
            pausestate = false;

            for (Sprite s: rubyCollectable)
            {
                //The loop iterates through the arraylist and sets the position of each sprite based off the index positions
                //given in the initial arrays
                if (currentLevel == 1) {
                    s.setX(ruby1x[index]);
                    s.setY(ruby1y[index]);
                    s.show();
                }else if (currentLevel == 2) {
                    s.setX(ruby2x[index]);
                    s.setY(ruby2y[index]);
                    s.show();
                }
                else if (currentLevel == 3 || currentLevel == 4){
                    s.hide();
                }
                else {
                    System.out.println("Error loading the map");
                    s.hide();
                }
                index++;

            }

            //READ COMMENTS ABOVE FOR MORE CONTEXT ON BELOW CODE

            int crab1x[] = {1279, 2153, 3387, 3568};
            int crab1y[] = {313, 313, 89, 248};
            index = 0;



            for (Sprite s: crab){

                s.setVelocityX(-0.05f);
                s.setVelocityY(0);
                if (currentLevel == 1){

                    s.setX(crab1x[index]);
                    s.setY(crab1y[index]);
                    s.show();
                }
                else if (currentLevel == 2){
                    s.hide();
                }
                else if (currentLevel == 3 || currentLevel == 4){
                    s.hide();
                }
                else {
                    System.out.println("Error loading the map");
                    s.hide();
                }


                index++;
            }

            int boss1x[] = {3958};
            int boss1y[] = {345};
            index = 0;

            for (Sprite s: Bigboss){
                s.setVelocityX(-0.08f);
                s.setVelocityY(0);

                if (currentLevel == 1){
                    System.out.println("Spooky boss man is hiding from you");
                    s.hide();
                }
                else if (currentLevel == 2){
                    s.setX(boss1x[index]);
                    s.setY(boss1y[index]);
                    System.out.println("You feel the nonexistent eyes of Spooky Boss Man piercing your skull");
                    s.show();
                }
                else if (currentLevel == 3 || currentLevel == 4){
                    s.hide();
                }
                else {
                    System.out.println("Error loading big boss man, he must be too powerful for your system");
                    s.hide();
                }
                index++;

            }

            int thing1x[] = {285, 1098, 1939, 2415, 2754};
            int thing1y[] = {121, 345, 217, 345, 345};
            index = 0;

            for (Sprite s: EvilThing){
                s.setVelocityX(-0.04f);
                s.setVelocityY(0);
                s.show();
                if (currentLevel == 1){
                    System.out.println("Waiting for Level 2");
                    s.hide();
                } else if (currentLevel == 2) {
                    s.setX(thing1x[index]);
                    s.setY(thing1y[index]);
                    s.show();
                }
                else if (currentLevel == 3 || currentLevel == 4){
                    s.hide();
                }
                else {
                    System.out.println("error");
                }
                index++;
            }
        }

    }
    
    /**
     * Draw the current state of the game. Note the sample use of
     * debugging output that is drawn directly to the game screen.
     */
    public void draw(Graphics2D g)
    {    	
    	// Be careful about the order in which you draw objects - you
    	// should draw the background first, then work your way 'forward'
        int xo = -(int)player.getX() + 200;
        int yo = -(int)player.getY() + 200;

        //if the current level is one of the menu screens

        if (currentLevel == 0 || currentLevel == 3 || currentLevel == 4){
            if (currentLevel == 0){
             /*   Font newfont = new Font("SansSerif", Font.BOLD, 40);
                g.setColor(Color.black);
                g.setFont(newfont);*/
/*                String title = String.format("ANTONIO THE LEVITATING PIRATE");
                String startup = String.format("Jump into the skull to start!");
                g.setColor(Color.darkGray);
                g.drawString(title, 376, 27 );*/
                g.drawImage(menu, 0, 0,  null);

                // Draws all the necessary sprites

                player.setOffsets(xo, yo);
                player.setScale(scale);
                player.draw(g);
                tmap.draw(g,xo,yo);
                goldSkull.setOffsets(xo, yo);
                goldSkull.draw(g);

            }
            else if (currentLevel == 3){

                // Screen Specific draws for Death and Win Screens
                tmap.draw(g,xo,yo);
                g.drawImage(winscreen, 0, 0, null);
                pausestate = true;

            } else if (currentLevel == 4) {

                tmap.draw(g,xo,yo);
                g.drawImage(deathscreen, 0, 0, null);
                pausestate = true;
            }

        }

        // Apply offsets to sprites then draw them
        if (currentLevel == 1 || currentLevel == 2){
            pausestate = false;

            // First work out how much we need to shift the view in order to
            // see where the player is. To do this, we adjust the offset so that
            // it is relative to the player's position along with a shift

            //The above has been moved somewhere else within the game

            //Below we draw the background

            g.drawImage(level1, 0,0, null);


            //Following code draws the paralax scrolling clouds of the background

            int para1x = (int)(xo * 0.4); //Sets the speed of the scrolling in the X axis, same below but for Y
            int para1y = (int)(yo * 0.4);

            int repeater = (getWidth()/paralax1.getWidth(null) + 3); // Makes image repeat after it has shown the full length

            for (int i = 0; i < repeater; i++) {
                g.drawImage(paralax1, para1x + i * paralax1.getWidth(null), para1y, null); //Draws the image and uses the predefined speeds above.

                //THIS CODE IS REPEATED BELOW, COMMENTS WILL NOT BE REPEATED

            }
            // Apply offsets to tile map and draw  it
            tmap.draw(g,xo,yo);

            // Apply offsets to player and draw
            player.setOffsets(xo, yo);
            player.setScale(scale);
            player.draw(g);

            //Apply all offsets to enemies and sprites in arraylists and draw
            for (Sprite s: clouds)
            {
                s.setOffsets(xo,yo);
                s.draw(g);
            }

            for (Sprite s: rubyCollectable){
                s.setOffsets(xo, yo);
                s.draw(g);
            }

            for (Sprite s: crab){
                s.setOffsets(xo, yo);
                s.draw(g);
            }

            for (Sprite s: EvilThing){
                s.setOffsets(xo, yo);
                s.draw(g);
            }

            for (Sprite s: Bigboss){
                s.setOffsets(xo, yo);
                s.draw(g);
            }




            //draw goal sprite
            goldSkull.setOffsets(xo, yo);
            goldSkull.draw(g);

            //LEVEL SPECIFIC PARALAX SCROLLING

            if (currentLevel == 1){
                int para2x = (int)(xo * 0.9);
                int para2y = (int)(yo * 0.9);

                int repeaterFront = (getWidth()/paralaxCloudFront.getWidth(null) + 5);

                for (int i = 0; i < repeaterFront; i++) {
                    g.drawImage(paralaxCloudFront, para2x + i * paralaxCloudFront.getWidth(null), para2y, null);
                }
            }

            if (currentLevel == 2){
                int para3x = (int)(xo * 0.9);
                int para3y = (int)(yo * 0.9);

                int repeaterWave = (getWidth()/paralaxLevel2.getWidth(null)+ 5);

                for (int i = 0; i < repeaterWave; i++) {
                    g.drawImage(paralaxLevel2, para3x + i * paralaxLevel2 .getWidth(null), para3y, null);
                }
            }




            // Show score and status information
            String msg = String.format("Score: %d", rubyCount);
            g.setColor(Color.darkGray);
            g.drawString(msg, getWidth() - 100, 50);
        }

        //Initial code for a health system, images are still available in folder should you want to uncomment this code and see how it would have worked
        // please note that the vignettes will not be the right size. They are fully functional otherwise and can be used as an indicator for health

/*        if (health == 3 || health == 2){
            g.drawImage(hurtVignettelow, 0, 0, null);
        } else if (health == 1) {
            g.drawImage(hurtVignetteHigh, 0, 0, null);
        }*/
/*        else{
            System.out.println("RANDOM UNKNOWN ERROR: This game should work but unfortunately ");
        }*/

        //Personal Note: Let's be honest, this might not work at all. At the moment nothing is running
        //Tally Count for failures: |||||||||||||||||||

        
        if (debug)
        {


        	// When in debug mode, you could draw borders around objects
            // and write messages to the screen with useful information.
            // Try to avoid printing to the console since it will produce 
            // a lot of output and slow down your game.
            tmap.drawBorder(g, xo, yo, Color.black);

            g.setColor(Color.red);
        	player.drawBoundingBox(g);
        
        	g.drawString(String.format("Player: %.0f,%.0f", player.getX(),player.getY()),
        								getWidth() - 100, 70);
        	
        	drawCollidedTiles(g, tmap, xo, yo);
        }

    }

    public void drawCollidedTiles(Graphics2D g, TileMap map, int xOffset, int yOffset)
    {
		if (collidedTiles.size() > 0)
		{	
			int tileWidth = map.getTileWidth();
			int tileHeight = map.getTileHeight();
			
			g.setColor(Color.blue);
			for (Tile t : collidedTiles)
			{
				g.drawRect(t.getXC()+xOffset, t.getYC()+yOffset, tileWidth, tileHeight);
			}
		}
    }
	
    /**
     * Update any sprites and check for collisions
     * 
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */    
    public void update(long elapsed)
    {
    	if (pausestate){
            return;
        }

        //Initialise sounds
        Sound hurt = new Sound("sounds/hurt.wav");
        Sound lose = new Sound("sounds/lose.wav");
        Sound scoreUp = new Sound("sounds/scoreUp.wav");
        Sound winCon = new Sound("sounds/win.wav");


        // Make adjustments to the speed of the sprite due to gravity
        player.setVelocityY(player.getVelocityY()+(gravity*elapsed));


        // Set sprite animation speeds

       	player.setAnimationSpeed(0.5f);

        skull.setAnimationSpeed(1.0f);

        ruby.setAnimationSpeed(0.2f);

        crabWalk.setAnimationSpeed(0.3f);

        thingWalk.setAnimationSpeed(0.1f);

        bossP.setAnimationSpeed(0.1f);

        skull.setAnimationSpeed(0.6f);


        //GOAL SPRITE POSITION SETS

        if (currentLevel == 0){
            goldSkull.setX(73);
            goldSkull.setY(125);
        }

        if (currentLevel == 1){
            goldSkull.setX(2849);
            goldSkull.setY(289);
        }
        else if (currentLevel == 2){
            goldSkull.setX(3638);
            goldSkull.setY(270);
        }

        //Set code for when player and a collectable collide

        for (Sprite s: rubyCollectable){
            s.update(elapsed);
            if (boundingBoxCollision(player, s)){
                s.setY(5000); //Set the position to an out of reach area to avoid the infinite score glitch
                s.setX(5000);
                s.hide();
                rubyCount = rubyCount + 1;
                scoreUp.start();
            }
        }

        // Same is done for all enemies, except damage is done this time
        for (Sprite s: crab){
            s.setVelocityY(s.getVelocityY()+(gravity*elapsed));
            s.update(elapsed);

            if (boundingBoxCollision(player, s) && s.isVisible()){

                // If you aren't CHEATING by using the debug mode, hitting an enemy will damage you
                if (!debug){
                    health = health - 1;
                    hurt.start();
                }
                player.setVelocity(-0.2f, -0.1f);
                player.setAnimation(damaged);
                if (health <= 0){
                    // if your health drops to 0 the game is lost (logic is dealt with elsewhere, I'm just inefficient and decided to also put the code here)
                    lose.start();
                }
                else{

                }
                s.stop();
                s.hide();
            }
            handleScreenEdge(s, tmap, elapsed);
            checkTileCollision(s, tmap, true);
        }

        //THE ABOVE CODE IS REPEATED FOR THE METHODS BELOW, I'm not going to repeat these comments

        for (Sprite s: EvilThing){
            s.setVelocityY(s.getVelocityY()+(gravity*elapsed));
            s.update(elapsed);

            if (boundingBoxCollision(player, s) && s.isVisible()){

                if (!debug){
                    health = health - 1;
                    hurt.start();
                }
                player.setVelocity(-0.2f, -0.1f);
                player.setAnimation(damaged);
                if (health <= 0){
                    lose.start();
                }
                else{

                }
                s.stop();
                s.hide();
            }
            handleScreenEdge(s, tmap, elapsed);
            checkTileCollision(s, tmap, true);
        }

        for (Sprite s: Bigboss){
            s.setVelocityY(s.getVelocityY()+(gravity*elapsed));
            s.update(elapsed);

            if (boundingBoxCollision(player, s) && s.isVisible()){
                if (!debug){
                    hurt.start();
                    health = health - 3;

                }
                player.setVelocity(-0.2f, -0.1f);
                player.setAnimation(damaged);
                if (health <= 0){

                    lose.start();
                }
                else{

                }
/*                s.stop();
                s.hide();*/
            }
            handleScreenEdge(s, tmap, elapsed);
            checkTileCollision(s, tmap, true);
        }

        //Update GoldSkull
        goldSkull.update(elapsed);


        //If the player hits the Gold Skull the next level will show. This goes until level 3 which is considered to be the win screen
        if (boundingBoxCollision(player, goldSkull)){
            winCon.start();
            if (goldSkull.isVisible()){
                goldSkull.hide();
                currentLevel++;
                initialiseGame();
            }
        }

        //If the player's health drops to 0 or below, the current level is set to 4 and the end screen shows.
        if (health <= 0){
            pausestate = true;
            prevState = currentLevel;
            currentLevel = 4;
            initialiseGame();
        }


        if (jump)
       	{
               //Below is the fix for infinite jump
               if (onGround){
                   player.setVelocityY(upJump);
                   player.setAnimation(jumping);
                   jump = false; // Detects whether player is still jumping
                   onGround = false; // Detects if player is on the ground
                   player.setVelocityY(player.getVelocityY());
               }


        }
       	
       	if (moveRight) 
       	{

       		player.setVelocityX(moveSpeed);
            player.setAnimation(move);
       	}

        else if (moveLeft)
        {
            // Left Movement is just reversal of the right movement
            player.setVelocityX(-moveSpeed);
            player.setAnimation(move);
        }
       	else
       	{
       		player.setVelocityX(0);
       	}


       	
                
       	for (Sprite s: clouds)
       		s.update(elapsed);


        // Now update the sprites animation and position
        player.update(elapsed);
       
        // Then check for any collisions that may have occurred
        handleScreenEdge(player, tmap, elapsed);
        checkTileCollision(player, tmap, false);
    }
    
    
    /**
     * Checks and handles collisions with the edge of the screen. You should generally
     * use tile map collisions to prevent the player leaving the game area. This method
     * is only included as a temporary measure until you have properly developed your
     * tile maps.
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     * @param elapsed	How much time has gone by since the last call
     */
    public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed)
    {
    	// This method just checks if the sprite has gone off the bottom screen.
    	// Ideally you should use tile collision instead of this approach
    	
    	float difference = s.getY() + s.getHeight() - tmap.getPixelHeight();
        if (difference > 0)
        {
        	// Put the player back on the map according to how far over they were
        	s.setY(tmap.getPixelHeight() - s.getHeight() - (int)(difference)); 
        	
        	// and make them bounce
/*        	s.setVelocityY(-s.getVelocityY()*0.75f);*/
        }
    }
    
    
     
    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     * 
     *  @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) 
    { 
    	int key = e.getKeyCode();

        // Coding is fun, which is why this looks like a mess
		switch (key)
		{
			case KeyEvent.VK_UP     :
                if (onGround){
                    jump = true;
                    Sound jumpup = new Sound("sounds/jump.wav");
                    jumpup.start();
                    onGround = false;
                }
                break; // Above is more code to stop the player from infinite jumping and only jumping while they're on the floor
			case KeyEvent.VK_RIGHT  : moveRight = true; break;
            case KeyEvent.VK_LEFT   : moveLeft = true; break;
			case KeyEvent.VK_S 		: Sound s = new Sound("sounds/caw.wav"); 
									  s.start();
									  break;
			case KeyEvent.VK_ESCAPE : stop(); break;
			case KeyEvent.VK_B 		: debug = !debug; break; // Flip the debug state
            case KeyEvent.VK_1      : currentLevel = 1; initialiseGame(); break;
            case KeyEvent.VK_2      : currentLevel = 2; initialiseGame(); break;
			default :  break;
		}
    
    }

    /** Use the sample code in the lecture notes to properly detect
     * a bounding box collision between sprites s1 and s2.
     * 
     * @return	true if a collision may have occurred, false if it has not.
     */
    public boolean boundingBoxCollision(Sprite s1, Sprite s2)
    {
    	return ((s1.getX() +s1.getImage().getWidth(null) > s2.getX())) &&
                (s1.getX() < (s2.getX() + s2.getImage().getWidth(null))) &&
                ((s1.getY() + s1.getImage().getHeight(null) > s2.getY())) &&
                (s1.getY() < s2.getY() + s2.getImage().getHeight(null));

        //original bounding box collision code below
        //solution provided above is adapted from this

        //return ((x + width) => sp.x) && (x <= sp.x + sp.width) &&
        //          (y + height) => sp.y) &&
        //              (y <= sp.y + sp.height));

    }
    
    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'. Initial functionality is limited...
     *
     * @param s    The Sprite to check collisions for
     * @param tmap The tile map to check
     */

    public void checkTileCollision(Sprite s, TileMap tmap, boolean isEnemy)
    {
        //In this method you can find the carnage which was me trying to figure out collision detection


    	// Empty out our current set of collided tiles
    	collidedTiles.clear();
    	onGround = false;
    	// Take a note of a sprite's current position
    	float sx = s.getX();
    	float sy = s.getY();

        float nextX = s.getX() + s.getVelocityX();
        float nextY = s.getY() + s.getVelocityY();
    	
    	// Find out how wide and how tall a tile is
    	float tileWidth = tmap.getTileWidth();
    	float tileHeight = tmap.getTileHeight();

        //////////////////////////////////////////////////////////////////////////////////////////////////////
        //Top Left Collision
    	// Divide the sprite’s x coordinate by the width of a tile, to get
    	// the number of tiles across the x axis that the sprite is positioned at 
    	int	xtile = (int)(sx / tileWidth);
    	// The same applies to the y coordinate
    	int ytile = (int)(sy / tileHeight);
    	
    	// What tile character is at the top left of the sprite s?
    	Tile tl = tmap.getTile(xtile, ytile);
    	
    	
    	if (tl != null && tl.getCharacter() != '.') // If it's not a dot (empty space), handle it
    	{
    		// Here we just stop the sprite.
            if (!isEnemy){
                s.stop();
                collidedTiles.add(tl);

                // You should move the sprite to a position that is not colliding
                s.shiftX(2.0f);
                s.shiftY(2.0f);
            }
            else if (isEnemy){ //If the colliding sprite is an enemy then it will reverse the X velocity meaning that the enemy cannot jump
                s.setVelocityX(-s.getVelocityX());
                s.setY((s.getY()+1));
            }

    	}
    	////////////////////////////////////////////////////////////////////////////////////////////////////////////


        // Bottom Left Collision
    	// We need to consider the other corners of the sprite
    	// The above looked at the top left position, let's look at the bottom left.
    	xtile = (int)(sx / tileWidth);
    	ytile = (int)((sy + s.getHeight())/ tileHeight);
    	Tile bl = tmap.getTile(xtile, ytile);
    	
    	// If it's not empty space
     	if (bl != null && bl.getCharacter() != '.') 
    	{
    		// Let's make the sprite bounce
/*    		s.setVelocityY(-s.getVelocityY()*0.6f); // Reverse velocity */
            s.setVelocityY(0);
    		collidedTiles.add(bl);
            if (!isEnemy){
/*                s.setY((float) (s.getY() - 1));*/
                s.setY(ytile * tileHeight - player.getHeight());
/*                s.setY(s.getY() - 1);*/
            } else if (isEnemy) {
                s.setY(s.getY() - 1);
            }



            if (!isEnemy){
                isJump = true;
            }

    	}
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////



        xtile = (int)((sx + s.getWidth())/tileWidth);
        ytile = (int)(sy/ tileHeight);

        Tile tr = tmap.getTile(xtile, ytile);
        if (tr != null && tr.getCharacter() != '.'){

            if (!isEnemy){
                s.stop();
                collidedTiles.add(tr);

                s.shiftX(-2.0f);
                s.shiftY(2.0f);
            } else if (isEnemy){
                s.setVelocityX(-(s.getVelocityX())); //If the colliding sprite is an enemy then it will reverse the X velocity meaning that the enemy cannot jump
                s.setY((s.getY()+1));
            }


        }



        xtile = (int)((sx + s.getWidth())/ tileWidth);
        ytile = (int)((sy + s.getHeight())/ tileHeight);
        Tile br = tmap.getTile(xtile, ytile);


        if (br != null && br.getCharacter() != '.'){

/*            s.setVelocityY(-s.getVelocityY()*0.6f); // Reverse velocity*/
/*            System.out.println("Colliding Bottom Right");*/
            s.setVelocityY(0);
            collidedTiles.add(br);

/*            onGround = true;*/


            if (!isEnemy){
/*                s.setY((float) (s.getY() - 1));*/
/*                s.setY(s.getY() - 1);*/
                s.setY(ytile * tileHeight - player.getHeight());
            } else if (isEnemy) {
                s.setY(s.getY() - 1);
            }



            // Can't remember why I put this here, I don't think it does anything but I'm leaving it in because any
            // adjustment I make now could open a rip in the spacetime continuum
            //
            if (!isEnemy){
                isJump = true;
            }

        }
        if (br != null && br.getCharacter() != '.' || bl != null && bl.getCharacter() != '.'){
            onGround = true;
        }


        //

    }


	public void keyReleased(KeyEvent e) { 

		int key = e.getKeyCode();

		switch (key)
		{
			case KeyEvent.VK_ESCAPE : stop(); break;
            case KeyEvent.VK_RIGHT  : moveRight = false; player.setAnimation(idle); break;
            case KeyEvent.VK_LEFT   : moveLeft = false; player.setAnimation(idle); break;
			case KeyEvent.VK_UP     : jump = false; player.setAnimation(idle); break;

			default :  break;
		}
	}


    /**
     * Below are the methods for a menu screen. Whether I got around to implementing them fully into the game loop
     * after the trouble with the player sprite, is currently unknown.
     * These contain methods for detecting mouse clicks on both the guiStart and guiQuit buttons
     *
     *
     * Further Note! These Methods are no longer in use, the code has been left in to demonstrate how this would have been done
     * had the mouse events been used
     * */


    // OOOOOOOHHHHH UNUSED CODE, I WONDER WHAT HAPPENS IF THESE ARE UNCOMMENTED

    //SURPRISE NOTHING HAPPENS, THEY DIDN'T FUNCTION

    //WELL DONE ON MAKING IT THROUGH MY CODE, NOT MANY PEOPLE HAVE MANAGED THAT BEFORE GIVING UP
    
/*    public void mouseClicked(MouseEvent e){
        int y= e.getX();
        int x = e.getY();

        if (start(x,y) && guiStart.isVisible()){
            guiStart.hide();
            guiQuit.hide();
            if (currentLevel == 0){
                currentLevel++;
                pausestate = false;
            } else if (currentLevel == 4) {
                pausestate=false;
            }
            initialiseGame();
        } else if (quit(x,y) && guiQuit.isVisible()) {
            currentLevel=0;
            stop();
        }
    }

    public boolean start(int x, int y){
        return ((guiStart.getX() + guiStart.getImage().getWidth(null) > x) && (guiStart.getX() < x) &&
                ((guiStart.getY() + guiStart.getImage().getWidth(null) > y) && (guiStart.getY() < y)));
    }

    public boolean quit(int x, int y){
        return ((guiQuit.getX() + guiQuit.getImage().getWidth(null) > x) && (guiQuit.getX() < x) &&
                ((guiQuit.getY() + guiQuit.getImage().getWidth(null) > y) && (guiQuit.getY() < y)));
    }*/

}
