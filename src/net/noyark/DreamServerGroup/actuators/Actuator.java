package net.noyark.DreamServerGroup.actuators;

import cn.nukkit.entity.Entity;

import java.util.Collection;

/**
 * @author zzz1999 @ AshManPro Project
 */
public interface Actuator {

    void perform(Collection<Entity> stream);

    void perform();


}
