package ch.unstable.ost.api.transport.types


import ch.unstable.ost.api.base.AbstractConnectionDeserializer

object ConnectionDeserializer : AbstractConnectionDeserializer() {
    override val sectionsField = "sections"
}
