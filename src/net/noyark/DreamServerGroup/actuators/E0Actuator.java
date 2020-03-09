package net.noyark.DreamServerGroup.actuators;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.item.EntityItem;
import net.noyark.DreamServerGroup.AshManPro;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author zzz1999 @ AshManPro Project
 */
public class E0Actuator implements Actuator {
    private List<String> levelExcept;

    public E0Actuator(List<String> levelExcept) {
        this.levelExcept = levelExcept;
    }

    public void perform(Collection<Entity> stream) {
        final int[] ic = {0};
        final int[] ec = {0};
        stream.forEach(e -> {
            if (!levelExcept.contains(e.getLevel().getFolderName())) {
                if (e instanceof EntityCreature && !(e instanceof Player) && AshManPro.eliminate_entity) {
                    if (AshManPro.eliminate_entity_force || !AshManPro.exemptedEntities.containsKey(e.getId())) {
                        e.close();
                        ec[0]++;
                    }
                } else if (e instanceof EntityItem && AshManPro.eliminate_items) {
                    if (!((EntityItem) e).getItem().hasCompoundTag() || !AshManPro.eliminate_items_except_nbtcontains) {
                        e.close();
                        ic[0]++;
                    }
                }
            }
        });
        String s = AshManPro.terminate_message;
        AshManPro.broadcastMessage(s, new Object[]{ic[0], ec[0]});
    }

    @Override
    public void perform() {
        Stream.Builder<Entity> build = Stream.builder();
        Server.getInstance().getLevels().values().forEach(l -> Stream.of(l.getEntities()).forEach(build::add));
        Stream<Entity> stream = build.build();
        final int[] ic = {0};
        final int[] ec = {0};
        stream.forEach(e -> {
            if (!levelExcept.contains(e.getLevel().getFolderName())) {
                if (e instanceof EntityCreature && !(e instanceof Player) && AshManPro.eliminate_entity) {
                    if (AshManPro.eliminate_entity_force || !AshManPro.exemptedEntities.containsKey(e.getId())) {
                        e.close();
                        ec[0]++;
                    }
                } else if (e instanceof EntityItem && AshManPro.eliminate_items) {
                    if (!((EntityItem) e).getItem().hasCompoundTag() || !AshManPro.eliminate_items_except_nbtcontains) {
                        e.close();
                        ic[0]++;
                    }
                }
            }
        });
        String s = AshManPro.terminate_message;
        AshManPro.broadcastMessage(s, new Object[]{ic[0], ec[0]});
    }
}
