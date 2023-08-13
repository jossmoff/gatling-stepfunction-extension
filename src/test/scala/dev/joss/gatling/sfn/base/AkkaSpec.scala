package dev.joss.gatling.sfn.base

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}

class AkkaSpec
    extends TestKit(ActorSystem())
    with BaseSpec
    with ImplicitSender
    with EmptySession
