package com.odde.doughnut.factoryServices.quizFacotries.factories;

import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.Thing;
import com.odde.doughnut.entities.User;
import com.odde.doughnut.models.NoteViewer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ParentGrandLinkHelperImpl(User user, Link link, Thing parentGrandLink)
    implements ParentGrandLinkHelper {

  @Override
  public Thing getParentGrandLink() {
    return parentGrandLink;
  }

  @Override
  public List<Link> getCousinLinksAvoidingSiblings() {
    List<Note> linkedSiblingsOfSameLinkType = link.getThing().getLinkedSiblingsOfSameLinkType(user);
    return getUncles()
        .flatMap(
            p ->
                new NoteViewer(user, p.getSourceNote())
                    .linksOfTypeThroughReverse(link.getLinkType()))
        .filter(cousinLink -> !linkedSiblingsOfSameLinkType.contains(cousinLink.getSourceNote()))
        .collect(Collectors.toList());
  }

  private Stream<Link> getUncles() {
    List<Note> linkTargetOfType =
        new NoteViewer(user, link.getSourceNote())
            .linksOfTypeThroughDirect(List.of(link.getLinkType())).stream()
                .map(Thing::getTargetNote)
                .collect(Collectors.toList());
    return parentGrandLink
        .getSiblingLinksOfSameLinkType(user)
        .filter(cl1 -> !linkTargetOfType.contains(cl1.getSourceNote()));
  }
}
