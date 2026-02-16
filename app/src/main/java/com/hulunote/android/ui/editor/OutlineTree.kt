package com.hulunote.android.ui.editor

import com.hulunote.android.data.model.NavInfo

data class OutlineNode(
    val id: String,
    val parid: String?,
    val content: String,
    val order: Float,
    val isDisplay: Boolean,
    val depth: Int,
    val hasChildren: Boolean,
    val isCollapsed: Boolean,
)

object OutlineTree {

    /**
     * Build a flat display list from nav items with proper depth and ordering.
     * The tree is built using parid (parent ID) relationships.
     */
    fun buildDisplayList(
        navList: List<NavInfo>,
        rootNavId: String?,
        collapsedIds: Set<String>,
    ): List<OutlineNode> {
        if (navList.isEmpty()) return emptyList()

        // Build parent-to-children map
        val childrenMap = mutableMapOf<String, MutableList<NavInfo>>()
        for (nav in navList) {
            if (nav.isDelete) continue
            val parentId = nav.parid ?: ""
            childrenMap.getOrPut(parentId) { mutableListOf() }.add(nav)
        }

        // Sort children by same_deep_order
        for ((_, children) in childrenMap) {
            children.sortBy { it.sameDeepOrder }
        }

        // Flatten tree via DFS starting from root's children
        val result = mutableListOf<OutlineNode>()
        val rootId = rootNavId ?: ""

        fun dfs(parentId: String, depth: Int) {
            val children = childrenMap[parentId] ?: return
            for (child in children) {
                val childChildren = childrenMap[child.id]
                val hasChildren = !childChildren.isNullOrEmpty()
                val isCollapsed = child.id in collapsedIds

                result.add(
                    OutlineNode(
                        id = child.id,
                        parid = child.parid,
                        content = child.content,
                        order = child.sameDeepOrder,
                        isDisplay = child.isDisplay,
                        depth = depth,
                        hasChildren = hasChildren,
                        isCollapsed = isCollapsed,
                    )
                )

                if (hasChildren && !isCollapsed) {
                    dfs(child.id, depth + 1)
                }
            }
        }

        dfs(rootId, 0)
        return result
    }

    /**
     * Calculate order value for inserting a new node between two siblings.
     * Returns a float value between prev and next orders.
     */
    fun orderBetween(prevOrder: Float?, nextOrder: Float?): Float {
        val prev = prevOrder ?: 0f
        val next = nextOrder ?: (prev + 200f)
        return (prev + next) / 2f
    }

    /**
     * Find the previous visible sibling at the same depth under the same parent.
     */
    fun findPreviousSibling(
        displayList: List<OutlineNode>,
        index: Int,
    ): OutlineNode? {
        if (index <= 0) return null
        val current = displayList[index]
        for (i in index - 1 downTo 0) {
            val node = displayList[i]
            if (node.parid == current.parid && node.depth == current.depth) return node
            if (node.depth < current.depth) break
        }
        return null
    }

    /**
     * Find the next visible sibling at the same depth under the same parent.
     */
    fun findNextSibling(
        displayList: List<OutlineNode>,
        index: Int,
    ): OutlineNode? {
        if (index >= displayList.size - 1) return null
        val current = displayList[index]
        for (i in index + 1 until displayList.size) {
            val node = displayList[i]
            if (node.parid == current.parid && node.depth == current.depth) return node
            if (node.depth < current.depth) break
        }
        return null
    }
}
