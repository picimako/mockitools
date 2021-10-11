package com.github.picimako.mockitools.services

import com.intellij.openapi.project.Project
import com.github.picimako.mockitools.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
