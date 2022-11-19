package com.wegame.mmorpg.component;


import com.wegame.mmorpg.logic.AStar;
import lombok.Data;

@Data
public class JoystickComponent {
    private AStar aStar;
    private int xPos = 0;
    private int yPos = 0;
}

