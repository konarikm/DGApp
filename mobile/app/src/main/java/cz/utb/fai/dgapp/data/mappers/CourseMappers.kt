package cz.utb.fai.dgapp.data.mappers

import cz.utb.fai.dgapp.data.local.Converters
import cz.utb.fai.dgapp.data.local.CourseEntity
import cz.utb.fai.dgapp.data.remote.CourseCreateApiDto
import cz.utb.fai.dgapp.data.remote.CourseApiDto
import cz.utb.fai.dgapp.domain.Course

private val converters = Converters()

// REST -> Domain
fun CourseApiDto.toDomain(): Course {
    return Course(
        id = this.id,
        name = this.name,
        location = this.location,
        description = this.description,
        numberOfHoles = this.numberOfHoles,
        parValues = this.parValues
    )
}

// Domain -> REST
fun Course.toApiDto(): CourseCreateApiDto {
    return CourseCreateApiDto(
        name = this.name,
        location = this.location,
        description = this.description,
        numberOfHoles = this.numberOfHoles,
        parValues = this.parValues
    )
}

// Local -> Domain
fun CourseEntity.toDomain(): Course {
    val parValues = converters.toIntList(this.parValuesJson)

    return Course(
        id = this.id,
        name = this.name,
        location = this.location,
        description = this.description,
        numberOfHoles = this.numberOfHoles,
        parValues = parValues
    )
}

// Domain -> Local
fun Course.toEntity(): CourseEntity {
    val parValues = converters.fromIntList(this.parValues)

    return CourseEntity(
        id = this.id,
        name = this.name,
        location = this.location,
        description = this.description,
        numberOfHoles = this.numberOfHoles,
        parValuesJson = parValues
    )
}