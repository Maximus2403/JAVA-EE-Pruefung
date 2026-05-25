package iu.piisj.kundenverwaltung.project;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsobjekt für die View — repräsentiert ein Projekt mit Einrückungstiefe.
 * Ermöglicht unbegrenzt tiefe Hierarchien ohne rekursives XHTML.
 */
public class ProjectNode {

    private final Project project;
    private final int     depth;

    public ProjectNode(Project project, int depth) {
        this.project = project;
        this.depth   = depth;
    }

    public Project getProject() { return project; }
    public int     getDepth()   { return depth; }

    // Einrückung in rem für inline-style
    public String getIndentStyle() {
        return depth == 0 ? "" : "margin-left:" + (depth * 1.5) + "rem;padding-left:0.75rem;border-left:3px solid #17C3B2;";
    }

    public boolean isRoot() { return depth == 0; }

    /**
     * Baut eine flache Liste aus dem Projektbaum — Tiefensuche.
     */
    public static List<ProjectNode> flatten(List<Project> roots) {
        List<ProjectNode> result = new ArrayList<>();
        for (Project root : roots) {
            addNode(root, 0, result);
        }
        return result;
    }

    private static void addNode(Project project, int depth, List<ProjectNode> result) {
        result.add(new ProjectNode(project, depth));
        for (Project child : project.getSubProjects()) {
            addNode(child, depth + 1, result);
        }
    }
}
