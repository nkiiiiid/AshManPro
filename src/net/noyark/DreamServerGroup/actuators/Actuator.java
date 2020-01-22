package net.noyark.DreamServerGroup.actuators;

import cn.nukkit.entity.Entity;

import java.util.stream.Stream;

/**
 * @author zzz1999 @ AshManPro Project
 */
public interface Actuator {

    void perform(Stream<Entity> stream);

    void perform();


}
