package pt.isel.ls.project.api.winterboot.annotations.mappings

@Target(AnnotationTarget.FUNCTION)
annotation class PostMapping(val path: String)
