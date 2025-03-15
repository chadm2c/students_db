package org.example

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class Student(
    @JsonProperty("id") val id : Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("age") val age : Int,
    @JsonProperty("faculty") val faculty : String
)
data class Students(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "student")
    @JsonProperty("student") val students: List<Student>
)
fun main() {
    val xmlMapper = XmlMapper()
    val students: Students = xmlMapper.readValue(
        object {}.javaClass.getResourceAsStream("/students.xml"),
        Students::class.java
    )

    val url = "jdbc:postgresql://localhost:5432/students"
    val user = "postgres"
    val password = "chadmany20"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, password)
        val sql = "INSERT INTO  student_info (id, name, age, faculty) VALUES (?, ?, ?, ?)"
        val preparedStatement: PreparedStatement = connection.prepareStatement(sql)

        for (student in students.students) {
            preparedStatement.setInt(1, student.id)
            preparedStatement.setString(2, student.name)
            preparedStatement.setInt(3, student.age)
            preparedStatement.setString(4,student.faculty)
            preparedStatement.executeUpdate()
        }

        println("Students inserted successfully!")
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}