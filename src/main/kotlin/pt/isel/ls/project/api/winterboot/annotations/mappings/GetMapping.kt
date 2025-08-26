package pt.isel.ls.project.api.winterboot.annotations.mappings

@Target(AnnotationTarget.FUNCTION)
annotation class GetMapping(val path: String)
