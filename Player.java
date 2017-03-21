/******
* name: Patrick Au, James Long
* date: March 2017
* code: ICS4U1
* note: Playable Entity class
*******/
import java.awt.*;
import java.awt.event.*;

class Player extends Entity {
	private boolean[] keysPressed = new boolean[4]; //whether directional keys are pressed or not: [up][down][left][right]
	private final double GRAVITY = 30.0 / GameScreen.FPS;
	private final double J_SPD = 480.0 / GameScreen.FPS;
	private final double J_SPD_MIN = 240.0 / GameScreen.FPS;
	private final double M_SPD = 120.0 / GameScreen.FPS;
	private final int bSize = Block.getSize();
	private Vector2 tl = new Vector2();
	private Vector2 br = new Vector2();
	private boolean[] boundsFlags = new boolean[4]; //whether ground is detected in a direction [up][down][left][right]
	
	Player() {
		super();
		this.pos.X = this.pos.Y = bSize;
		this.acc = new Vector2(0.0, GRAVITY);
	}	// end constructor()

	@Override // Superclass: Entity
	public void advance() {
		updateVectors();
		this.vel.add(this.acc);
		updateBounds();
		if(vel.Y > 0.0) { // Only Checks "Below" the Player for Solid Earth
			posInt[0] = (int)Math.round(pos.X + Block.getSize() / 2) / Block.getSize();
			posInt[1] = (int)Math.round(pos.Y + Block.getSize() / 2) / Block.getSize() + 1;
			if(GameScreen.getBlocks(posInt[1], posInt[0]).getBlock() == 1)
				// Reduce Velocity to Size of Gap between Player and Block to avoid going through the ground
				this.vel.Y = Math.min(posInt[1] * Block.getSize() - pos.Y - Block.getSize(), vel.Y);
		}	// end if
		move(this.vel);
	}	// end method advance

	public void accl(final Vector2 velo) {
		this.vel.add(velo);
	}	// end method accl

	public boolean checkBlock(int dir) {
		if(dir & 1) {	// Horizontal
		}
		else {
			return
				(GameScreen.getBlocks(getArrDx(pos.Y, true) + 1, getArrDx(pos.X, false)).getBlock() == 1) ||
				(GameScreen.getBlocks(getArrDx(pos.Y, true) + 1, getArrDx(pos.X,  true)).getBlock() == 1);
		}	// end if
	}	// end method checkBlock

	@Override // Superclass: Entity
	public void draw(Graphics g) {
		// Hardcode image for Demo
		g2D = (Graphics2D)g;
		g2D.fillRect((int)tl.X, (int)tl.Y, (int)(br.X - tl.X), (int)(br.Y - tl.Y));
		g2D.drawImage(Images.demo[2], (int)Math.round(pos.X), (int)Math.round(pos.Y), Block.getSize(), Block.getSize(), null);
	}	// end method draw

	public int getArrDx(int val, boolean end) {
		return (val + ((end) ? Block.getSize() : 0)) / Block.getSize();
	}	// end method getArrDx

	public final Vector2 getVel() {
		return this.vel;
	}	// end method getVel

	public void move(final Vector2 disp) {
		if(disp.X > 0)
			disp.X = Math.min(disp.X, br.X - (pos.X + bSize));
		else
			disp.X = Math.max(disp.X, tl.X - pos.X);
		if(disp.Y > 0)
			disp.Y = Math.min(disp.Y, br.Y - (pos.Y + bSize));
		else
			disp.Y = Math.max(disp.Y, tl.Y - pos.Y);
		this.pos.add(disp);
	}	// end method move
	
	public void setKey(int indexToSet, boolean pressedDown)
	{
		keysPressed[indexToSet] = pressedDown;
	}	// end method setKey
	
	public void updateBounds()
	{
		for(int i = 0; i < boundsFlags.length; i++)
			boundsFlags[i] = false;
		tl.X = bSize * Math.floor(pos.X / bSize);
		tl.Y = bSize * Math.floor(pos.Y / bSize);
		br.X = bSize * Math.ceil((pos.X + bSize) / bSize);
		br.Y = bSize * Math.ceil((pos.Y + bSize) / bSize);
		for(int i = (int)(tl.X/bSize); i < (int)(br.X/bSize); i++)
		{
			if(GameScreen.getBlocks((int)tl.Y/bSize-1, i).getBlock() == 1)
				boundsFlags[0] = true;
			if(GameScreen.getBlocks((int)br.Y/bSize, i).getBlock() == 1)
				boundsFlags[1] = true;
		}
		for(int i = (int)(tl.Y/bSize); i < (int)(br.Y/bSize); i++)
		{
			if(GameScreen.getBlocks(i, (int)tl.X/bSize-1).getBlock() == 1)
				boundsFlags[2] = true;
			if(GameScreen.getBlocks(i, (int)br.X/bSize).getBlock() == 1)
				boundsFlags[3] = true;
		}
		if(!boundsFlags[0])
			tl.Y = tl.Y - bSize;
		if(!boundsFlags[1])
			br.Y = br.Y + bSize;
		if(!boundsFlags[2])
			tl.X = tl.X - bSize;
		if(!boundsFlags[3])
			br.X = br.X + bSize;
	}
	
	public void updateVectors() //move based on the keys currently being pressed
	{
		if(keysPressed[0])
		{
			if(Math.abs(vel.Y) < 1E-6) //if the player is on the ground
				accl(new Vector2(0.0, -1 * J_SPD));
		}
		else if(!keysPressed[0]) //"cut" the jump if the button is released early
		{
			if(vel.Y < -1 * J_SPD_MIN)
				vel.Y = (-1 * J_SPD_MIN);
		}
		//currently no actions for pressing down (this method will have to take into account fields later)
		if(keysPressed[2])
			move(new Vector2(-1 * M_SPD, 0.0));
		if(keysPressed[3])
			move(new Vector2(+1 * M_SPD, 0.0));
	}	// end method updateVectors
}	// end class
