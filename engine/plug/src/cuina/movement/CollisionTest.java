package cuina.movement;

import java.util.ArrayList;
import java.util.List;

import cuina.util.Rectangle;

public class CollisionTest
{
	private static Rectangle obj1;
	private static Rectangle block;

	public static void main(String[] args)
	{
		obj1 = new Rectangle(0, 30, 10, 10);
		block = new Rectangle(40, 12, 10, 10);
		
		move(obj1, 0, 0);
	}
	
	private static void move(Rectangle rect, int x, int y)
	{
		Rectangle other;
		other = testPathCollision(rect, x, y);
		System.out.println("Path-Collision: " + other);
		
		rect.x = x;
		rect.y = y;
		other = testCollision(rect);
		System.out.println("Target-Collision: " + other);
	}

	public static Rectangle testCollision(Rectangle self)
	{
		for(Rectangle other : getRectangles())
		{
			if (!other.equals(self) && self.intersects(other))
			{
				return other;
			}
		}
		return null;
	}
	
	public static Rectangle testPathCollision(Rectangle self, int x, int y)
	{
		for(Rectangle other : getRectangles())
		{
			if (other == self) continue;
			
			if (x == self.x)
			{
				// Sonderfall
				Rectangle rect;
				if (y > self.y)
					rect = new Rectangle(self.x, self.y, self.width, self.height + y);
				else
					rect = new Rectangle(self.x, y, self.width, self.y + self.height);
				
				if (rect.intersects(other)) return other;
			}
			else
			{
				// Steigung
				float dy = (y - self.y) / (float) (x - self.x);
				System.out.println("DY: " + dy);
				
				// obere Bewegungslinie
				int x1 = (dy > 0) ? self.x + self.width : self.x;
				int y1 = self.y;
				// untere Bewegungslinie
				int x2 = (dy > 0) ? self.x : self.x + self.width;
				int y2 = self.y + self.width;
				
				int cy1, cy2;
				cy1 = (int) (y1 + (other.x - x1) * dy);
				cy2 = (int) (y2 + (other.x - x2) * dy);
				if (other.y + other.height > cy1 && other.y < cy2) return other;
				
				cy1 = (int) (y1 + (other.x + other.width - x1) * dy);
				cy2 = (int) (y2 + (other.x + other.width - x2) * dy);	
				if (other.y > cy1 && other.y + other.height < cy2) return other;
			}
		}
		return null;
	}
	
	private static List<Rectangle> getRectangles()
	{
		List<Rectangle> list = new ArrayList<Rectangle>();
		list.add(obj1);
		list.add(block);
		return list;
	}
}
