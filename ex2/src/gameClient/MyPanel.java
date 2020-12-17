package gameClient;

import api.*;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import org.json.JSONException;
import org.json.JSONObject;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * This class extends JPanel Class that creates the panel for the game on the frame,
 * and implements ActionListener for receiving action events.
 */
public class MyPanel extends JPanel implements ActionListener {
	private Arena arena;
	private gameClient.util.Range2Range point;
	private Timer timer;

	/**
	 * Constructor used to start a timer for the repaint function.
	 */
	public MyPanel() {
		timer = new Timer(10, this);
		timer.start();
	}

	/**
	 * This method update the arena of the pokemon game.
	 * @param ar - the arena of the game
	 */
	public void update(Arena ar) {
		this.arena = ar;
		updateFrame();
	}

	/**
	 * This method update the frame and set it width and height of the pokemon game.
	 */
	private void updateFrame() {
		Range rx = new Range(20, this.getWidth() - 20);
		Range ry = new Range(this.getHeight() - 10, 150);
		Range2D frame = new Range2D(rx, ry);
		directed_weighted_graph g = arena.getGraph();
		point = Arena.w2f(g, frame);
	}

	/**
	 * This method overrides paintComponent function to paint the pokemons,
	 * graph, agents, info and scores on the frame.
	 * @param g - Graphics
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = this.getWidth();
		int h = this.getHeight();
		g.clearRect(0, 0, w, h);
		updateFrame();
		drawPokemons(g);
		drawGraph(g);
		drawAgents(g);
		drawInfo(g);
		drawScores(g);
	}

	/**
	 * This method draws info into the panel.
	 * @param g - Graphics
	 */
	private void drawInfo(Graphics g) {
		List<String> str = arena.get_info();
		if (str != null && !str.isEmpty() && str.get(0) != null) {
			String gameString = str.get(0);
			JSONObject gameJsonObject;
			try {
				gameJsonObject = new JSONObject(gameString);
				JSONObject gameJsonServer = gameJsonObject.getJSONObject("GameServer");
				int pokemons = gameJsonServer.getInt("pokemons");
				int moves = gameJsonServer.getInt("moves");
				int grade = gameJsonServer.getInt("grade");
				int level = gameJsonServer.getInt("game_level");
				long time = arena.getTime();
				int w = this.getWidth();
				int h = this.getHeight();

				g.setFont(new java.awt.Font("Verdana", Font.ITALIC, 13));
				g.setColor(Color.BLACK);
				g.drawString("Pokemons on graph : " + pokemons, (int)(w/4), (int)(h/12));
				g.drawString("Moves : " + moves, (int)(w/2), (int)(h/12));
				g.drawString("Total Grade: " + grade, (int)(w/4), (int)(h/8));
				g.drawString("Level : " + level, (int)(w/50), (int)(h/12));
				g.drawString("Time left: " + time / 1000, (int)(w/50), (int)(h/8));
				g.drawRect((int)(w/70),(int)(h/30),(int)(w/1.05),(int)(h/8));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method draws the graph into the panel.
	 * @param g - Graphics
	 */
	private void drawGraph(Graphics g) {
		directed_weighted_graph gg = arena.getGraph();
		for (node_data n : gg.getV()) {
			g.setColor(Color.blue);
			drawNode(n, 5, g);
			for (edge_data e : gg.getE(n.getKey())) {
				g.setColor(Color.gray);
				drawEdge(e, g);
			}
		}
	}

	/**
	 * This method draws the pokemons into the panel.
	 * @param g - Graphics
	 */
	private void drawPokemons(Graphics g) {
		List<CL_Pokemon> fs = arena.getPokemons();
		if (fs != null) {
			for (CL_Pokemon f : fs) {
				Point3D c = f.getLocation();
				int r = 10;
				g.setColor(Color.green);
				if (f.getType() < 0) {
					g.setColor(Color.orange);
				}
				if (c != null) {
					geo_location fp = this.point.world2frame(c);
					g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
				}
			}
		}
	}

	/**
	 * This method draws the agents into the panel.
	 * @param g - Graphics
	 */
	private void drawAgents(Graphics g) {
		List<CL_Agent> agents = arena.getAgents();
		List<String> str = arena.get_info();
		if (str != null && !str.isEmpty() && str.get(0) != null && agents != null) {
			String gameString = str.get(0);
			JSONObject gameJsonObject;
			try {
				gameJsonObject = new JSONObject(gameString);
				JSONObject gameJsonServer = gameJsonObject.getJSONObject("GameServer");
				int agentNumber = gameJsonServer.getInt("agents");
				g.setColor(Color.red);
				for (int i = 0; i < agentNumber; i++) {
					geo_location c = agents.get(i).getLocation();
					int r = 8;
					if (c != null) {
						geo_location fp = this.point.world2frame(c);
						g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
						g.drawString("" + i, (int) fp.x(), (int) fp.y() - 4 * r);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method draws the node into the panel.
	 * @param n - the current node_data to draw
	 * @param r - int
	 * @param g - Graphics
	 */
	private void drawNode (node_data n,int r, Graphics g){
		geo_location pos = n.getLocation();
		geo_location fp = this.point.world2frame(pos);
		g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
		g.drawString("" + n.getKey(), (int) fp.x(), (int) fp.y() - 4 * r);
	}

	/**
	 * This method draws the edge into the panel.
	 * @param e - the current edge_data to draw
	 * @param g - Graphics
	 */
	private void drawEdge (edge_data e, Graphics g){
		directed_weighted_graph gg = arena.getGraph();
		geo_location s = gg.getNode(e.getSrc()).getLocation();
		geo_location d = gg.getNode(e.getDest()).getLocation();
		geo_location s0 = this.point.world2frame(s);
		geo_location d0 = this.point.world2frame(d);
		g.drawLine((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
	}

	/**
	 * This method draws the scores into the panel.
	 * @param g - Graphics
	 */
	private void drawScores (Graphics g){
		List<CL_Agent> agents = arena.getAgents();
		int indexY = 0;
		if (agents != null) {
			for (CL_Agent agent : agents) {
				g.setColor(Color.BLACK);
				g.drawString("Agent :(" + agent.getID() + "). Score is: " + agent.getValue(), (int) (this.getWidth() / 1.3), (int)(this.getHeight()/12) + indexY);
				indexY += 20;
			}
		}
	}

	/**
	 * This method repaint the panel when each action is being performed.
	 * @param e - ActionEvent
	 */
	@Override
	public void actionPerformed (ActionEvent e) {
		repaint();
	}
}