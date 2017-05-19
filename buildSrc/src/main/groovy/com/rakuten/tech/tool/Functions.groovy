package com.rakuten.tech.tool

import org.codehaus.groovy.ast.expr.ConstantExpression

class Functions {
    static def findAllStatements(parent, spec) {
        def predicate = spec instanceof String ? { it == spec } : spec;
        parent.expression.arguments.expressions.get(0).code.statements.findAll {
            predicate(it.expression.method.value)
        }
    }

    static def findAllSnapshots(List dependencies) {
        def dependencyArgs = dependencies.collect {
            it.expression.arguments.expressions.get(0) // first argument
        }
        dependencyArgs == null ? [] :
                dependencyArgs.findAll { it instanceof ConstantExpression && it.value.contains ("SNAPSHOT") }
    }
}
