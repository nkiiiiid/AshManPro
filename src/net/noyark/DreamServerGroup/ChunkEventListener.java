package net.noyark.DreamServerGroup;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkUnloadEvent;

/**
 * @author zzz1999 @ AshManPro Project
 */
public class ChunkEventListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.getChunk().getEntities().values().stream()
                .filter(e -> e instanceof EntityCreature)
                .forEach(Entity::close);
    }
}
