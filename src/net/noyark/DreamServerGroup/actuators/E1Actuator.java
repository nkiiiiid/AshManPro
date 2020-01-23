package net.noyark.DreamServerGroup.actuators;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.level.Level;
import net.noyark.DreamServerGroup.AshManPro;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author zzz1999 @ AshManPro Project
 */
public class E1Actuator implements Actuator {
    private List<String> levelExcept;
    private int threshold;

    public E1Actuator(List<String> levelExcept, int threshold) {
        this.levelExcept = levelExcept;
        this.threshold = threshold;
    }

    @Override
    public void perform(Stream<Entity> stream) {
        final int[] ic = {0};
        final int[] ec = {0};
        stream.forEach(e -> {
            if (!levelExcept.contains(e.getLevel().getFolderName()) && e.getLevel().getPlayers().size() > threshold) {
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
        Server.getInstance().broadcastMessage(s.replaceAll("\\{ic}", String.valueOf(ic[0])).replaceAll("\\{ec}", String.valueOf(ec[0])), Server.getInstance().getOnlinePlayers().values());
    }

    @Override
    public void perform() {
        final int[] ic = {0};
        final int[] ec = {0};
        Server.getInstance().getLevels().values().stream()
                .filter(((Predicate<Level>) l -> l.getPlayers().size() > threshold)
                        .and(l -> !levelExcept.contains(l.getFolderName()))
                )
                .map(Level::getEntities)
                .flatMap(Arrays::stream)
                .forEach(e -> {
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
                );
        String s = AshManPro.terminate_message;
        Server.getInstance().broadcastMessage(s.replaceAll("\\{ic}", String.valueOf(ic[0])).replaceAll("\\{ec}", String.valueOf(ec[0])), Server.getInstance().getOnlinePlayers().values());
    }
}
