package net.noyark.DreamServerGroup;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkUnloadEvent;

import java.util.function.Predicate;

/**
 * @author zzz1999 @ AshManPro Project
 */
public class ChunkEventListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (!event.getChunk().getEntities().isEmpty()) {
            event.getChunk().getEntities().values().stream()
                    .filter(((Predicate<Entity>) e -> e instanceof EntityCreature && !(e instanceof Player) && AshManPro.eliminate_entity)
                            .and(e -> AshManPro.eliminate_entity_force || !AshManPro.exemptedEntities.containsKey(e.getId()))
                    )
                    .forEach(Entity::close);
        }
    }
}
