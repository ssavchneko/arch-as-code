package net.trilogy.arch.domain.c4;

import net.trilogy.arch.domain.Diffable;

public interface Entity extends Diffable, HasRelation, HasTag, HasIdentity {
    String getName();

    String getDescription();

    C4Type getType();
}
