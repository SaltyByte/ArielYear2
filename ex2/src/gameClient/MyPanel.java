package gameClient;


import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MyPanel extends JPanel implements Runnable  {
    private Arena arena;
    private gameClient.util.Range2Range point;

    public void update(Arena ar) {
        this.arena = ar;
        updateFrame();
    }

    private void updateFrame() {
        Range rx = new Range(20, this.getWidth() - 20);
        Range ry = new Range(this.getHeight() - 10, 150);
        Range2D frame = new Range2D(rx, ry);
        directed_weighted_graph g = arena.getGraph();
        point = Arena.w2f(g, frame);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);
        updateFrame();
        drawPokemons(g);
        drawGraph(g);
        drawAgants(g);
        drawInfo(g);
    }

    private void drawInfo(Graphics g) {
        List<String> str = arena.get_info();
        String dt = "none";
        for (int i = 0; i < str.size(); i++) {
            g.drawString(str.get(i) + " dt: " + dt, 100, 60 + i * 20);
        }
    }

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

    private void drawAgants(Graphics g) {
        List<CL_Agent> rs = arena.getAgents();
        //	Iterator<OOP_Point3D> itr = rs.iterator();
        g.setColor(Color.red);
        int i=0;
        while(rs!=null && i<rs.size()) {
            geo_location c = rs.get(i).getLocation();
            int r=8;
            i++;
            if(c!=null) {
                geo_location fp = this.point.world2frame(c);
                g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
                g.drawString(""+(i-1), (int)fp.x(), (int)fp.y()-4*r);
            }
        }
    }

    private void drawNode(node_data n, int r, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this.point.world2frame(pos);
        g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
        g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-4*r);
    }

    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = arena.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this.point.world2frame(s);
        geo_location d0 = this.point.world2frame(d);
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */


    @Override
    public void run() {
        while(true) {
            System.out.println("Run");
            repaint();
        }
    }
}
