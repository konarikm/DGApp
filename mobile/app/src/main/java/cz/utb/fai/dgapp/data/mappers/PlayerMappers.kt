package cz.utb.fai.dgapp.data.mappers

import cz.utb.fai.dgapp.data.local.PlayerEntity
import cz.utb.fai.dgapp.data.remote.PlayerApiDto
import cz.utb.fai.dgapp.domain.Player

// REST -> Domain
fun PlayerApiDto.toDomain(): Player {
    return Player(
        id = this.id,
        name = this.name,
        pdgaNumber = this.pdgaNumber,
        email = this.email
    )
}

// Domain -> REST
fun Player.toApiDto(): PlayerApiDto {
    return PlayerApiDto(
        id = this.id,
        name = this.name,
        pdgaNumber = this.pdgaNumber,
        email = this.email
    )
}

// Local -> Domain
fun PlayerEntity.toDomain(): Player {
    return Player(
        id = this.id,
        name = this.name,
        pdgaNumber = this.pdgaNumber,
        email = this.email
    )
}

// Domain -> Local
fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(
        id = this.id,
        name = this.name,
        pdgaNumber = this.pdgaNumber,
        email = this.email
    )
}