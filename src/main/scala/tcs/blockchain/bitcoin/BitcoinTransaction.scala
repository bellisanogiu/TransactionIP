package tcs.blockchain.bitcoin

import org.bitcoinj.core.{Sha256Hash, Transaction}
import org.bitcoinj.script.Script.ScriptType
import tcs.blockchain.{Transaction => TCSTransaction}
import java.util.Date
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * imported libreries for project 15
  */
  import net.liftweb.json._
  import scala.io.Source._

/**
  * Defines a transaction of the Bitcoin blockchain.
  *
  * @param hash Transaction hash
  * @param txSize Size of the transaction
  * @param inputs List of transaction inputs
  * @param outputs List of transaction outputs
  */
class BitcoinTransaction(
                          override val hash: String,
                          override val date: Date,

                          val txSize: Int,
                          val inputs: List[BitcoinInput],
                          val outputs: List[BitcoinOutput],
                          val lock_time: Long) extends TCSTransaction{

  /**
    * Returns the 'Relayed By' field representing the ip address of the node from which the explorer received the transaction    *
    * @return 'relayed by' value
    */
  def getIP(): Unit = {

    // Almost all resources exist under a given blockchain, and follow this pattern
    val url = "https://api.blockcypher.com/"          // url
    val protocol = "v1/"                              // blockcypher API version
    val coin = "btc/"                                 // coin
    val chain = "main/"                               // chain
    val txs = "txs/"                                  // transactions

    // bitcoinCypher complete url
    //val urlComplete: String = url + protocol + coin + chain + txs + hash.mkString
    //println(urlComplete)

    //val urlComplete = "https://api.blockcypher.com/v1/btc/main/txs/cbcf51d636b02bedbcdff9b229f2ea2f20e3cb193752714c415b1752c9a0ed32" // con relayed by
    val urlComplete = "https://api.blockcypher.com/v1/btc/main/txs/6961d06e4a921834bbf729a94d7ab423b18ddd92e5ce9661b7b871d852f1db74" // senza relayed by
    val jsonFromUrl = fromURL(urlComplete).mkString

    case class valueRelayedBy (relayed_by: String)

    // val format
    implicit val formats = DefaultFormats

    // convert a String to a JValue object
    val jValue = parse(jsonFromUrl)
    println(jValue)

    // extract the value 'Relayed By' as a JObject
    val relayedByExtract = jValue.extract[valueRelayedBy]
    println(relayedByExtract)

    // alternative method for extract country name (liftweb api)
    //val jsearch = (jValue \ "relayed_by").extract[valueRelayedBy]
    //println(jsearch)

    //return relayedByExtract.toString
    }

  def getIP2(): String = {


    // Almost all resources exist under a given blockchain, and follow this pattern
    val url = "https://api.blockcypher.com/"          // url
    val protocol = "v1/"                              // blockcypher API version
    val coin = "btc/"                                 // coin
    val chain = "main/"                               // chain
    val txs = "txs/"                                  // transactions

    // bitcoinCypher complete url
    val urlComplete: String = url + protocol + coin + chain + txs + hash.mkString
    //println(urlComplete)

    //val urlComplete = "https://api.blockcypher.com/v1/btc/main/txs/fe6c48bbfdc025670f4db0340650ba5a50f9307b091d9aaa19aa44291961c69f" // con relayed by
    //val urlComplete = "https://api.blockcypher.com/v1/btc/main/txs/ee475443f1fbfff84ffba43ba092a70d291df233bd1428f3d09f7bd1a6054a1f" // senza relayed by
    val jsonFromUrl = fromURL(urlComplete).mkString

    case class valueRelayedBy (relayed_by: String)

    /**
      * nuovo case class json
      */

      case class root(block_hash: String, block_height: Number, block_index: Number, hash: String, addresses: Array[String], total: Number, fees: Number, size: Number,preference: String,
                      relayed_by: String, confirmed: String, received: String, ver: Number, double_spend: Boolean, vin_sz: Number, vout_sz: Number, confirmations: Number,
                      confidence: Number, inpunts: Array[Object], outputs: Array[Object])

    // val format
    implicit val formats = DefaultFormats

    // convert a String to a JValue object
    val jValue = parse(jsonFromUrl)
    //println(jValue)

    // extract the value 'Relayed By' as a JObject
    //val relayedByExtract = jValue.extract[valueRelayedBy]
    //println(relayedByExtract)

    // alternative method for extract country name (liftweb api)
    //val jsearch = (jValue \\ "relayed_by").extract[valueRelayedBy]
    //val jsearch = ((jValue \ "country") \ "name").extract[String]
    var jsearch = (jValue \\ "relayed_by").children //.extract[String]
    //println(jsearch)
    if (jsearch.isEmpty == true) {
      return "nothing ip"
    }
    else {
     var jsearch = (jValue \ "relayed_by").extract[String]
      return jsearch
    }

  }

  /**
    * Returns the sum of all the input values.
    * If a "deep scan" was not performed, each input value is set to 0.
    *
    * @return Sum of all the input values
    */
  def getInputsSum(): Long = {
    inputs.map(input => input.value).reduce(_ + _)
  }

  /**
    * Returns the sum of all the input values.
    * If TxIndex is not set in your bitcoin client this method will not work
    *
    * @param blockchain instance of the BitcoinBlockchain
    * @return Sum of all the input values
    */
  def getInputsSumUsingTxIndex(blockchain: BitcoinBlockchain): Long = {
    var sum: Long = 0
    for(input <- inputs){
      sum += blockchain.getTransaction(input.getRedeemedTxHashAsString).getOutputValueByIndex(input.getRedeemedOutIndex)
    }
    return sum
  }

  /**
    * Returns the sum of all the output values.
    *
    * @return Sum of all the output values
    */
  def getOutputsSum(): Long = {
    outputs.map(output => output.value).reduce(_ + _)
  }

  /**
    * Returns the list containing all the hashes(as strings) of the input values.
    *
    * @return List of all the hashes(as strings) of the input values
    */
  def getInputsHashList(): List[String] = {
    (inputs.foldLeft(new ListBuffer[String])((list, a) => (list += a.getRedeemedTxHashAsString))).toList
  }


  def getOutputValueByIndex(index: Int): Long = {
    outputs.filter((a) => a.getIndex == index ) match{
      case element :: Nil => element.value
      case _ => 0
    }
  }


  /**
    * Returns the transaction lock time.
    *
    * @return Transaction lock time
    */
  def getLockTime(): Long = {
    lock_time
  }


  /**
    * Returns a string representation of the transaction,
    * including hash, size, list of inputs, and list of outputs.
    *
    * @return String representation of the object.
    */
  override def toString(): String = {
    val stringInputs: String = "[ " + inputs.map(i => i.toString() + " ") + "]"
    val stringOutputs: String = "[ " + outputs.map(o => o.toString() + " ") + "]"

    return hash + " " + txSize + " " + stringInputs + " " + stringOutputs
  }

  def printTransaction(): Unit = {
    val stringInputs: String = "[ " + inputs.map(i => "\n  " + i.toString()) + "\n]"
    val stringOutputs: String = "[ " + outputs.map(o =>"\n  " + o.toString()) + "\n]"
    println()
    println( "Hash: " +  hash)
    println( "TxSize: " + txSize)
    println( "LockTime: " + getLockTime())
    println( "InputsSum: " + getInputsSum())
    println( "OutputsSum: " + getOutputsSum())
    println( "StringInputs: " +  stringInputs)
    println( "StringOutputs: " +  stringOutputs)
    println()
  }

  def getPrintableTransaction(): String = {
    val stringInputs: String = "[ " + inputs.map(i => "\n  " + i.toString()) + "\n]"
    val stringOutputs: String = "[ " + outputs.map(o =>"\n  " + o.toString()) + "\n]"
    "\n" + "Hash: " +  hash + "\nTxSize: " + txSize + "\nLockTime: " + getLockTime() + "\nInputsSum: " + getInputsSum() + "\nOutputsSum: " + getOutputsSum() + "\nStringInputs: " +  stringInputs + "\nStringOutputs: " +  stringOutputs + "\n"
  }


  /**
    * Returns a boolean which states if the transaction is standard or not
    * @return True if the transaction is standard, false otherwise
    */
  def isStandard : Boolean = {
    transactionType match {
      case TxType.TX_STANDARD => hasStandardConditions
      case _ => false
    }
  }


  /**
    * Iterates through the output script list and checks if they are standard or not
    * @return the related type of the enumeration
    */
  def transactionType : TxType.Value = {
    outputs.foreach(out => {
      if(
        out.transOut.getScriptPubKey.getScriptType == ScriptType.P2SH ||
        out.transOut.getScriptPubKey.getScriptType == ScriptType.P2PKH ||
        out.transOut.getScriptPubKey.getScriptType == ScriptType.PUB_KEY ||
        out.transOut.getScriptPubKey.isSentToMultiSig ||
        out.transOut.getScriptPubKey.isOpReturn)
        { return TxType.TX_STANDARD }

      return TxType.TX_NONSTANDARD

    })

    return TxType.TX_NOTYPE
  }


  /**
    * Checks the size of a transaction
    * @return true if the size is less than 100,000 bytes, false otherwise
    */
  private def hasStandardTransactionSize : Boolean = {
    return txSize < 100000
  }


  /**
    * Checks the size of each script
    * @return true if the size is less than 1,650 bytes, false otherwise
    */
  private def hasStandardScriptSize : Boolean =  {
    outputs.foreach(out => {
      val size = out.outScript.getProgram.length
      if(size > 1650){
        return false
      }
    })
    return true
  }


  /**
    * Checks the number of signatures required
    * @return true if the number of signatures is less or equal than 3, false otherwise
    */
  private def hasStandardSignatures : Boolean =  {
    outputs.foreach(out => {
      if(out.transOut.getScriptPubKey.isSentToMultiSig){
        val signatures = out.transOut.getScriptPubKey.getNumberOfSignaturesRequiredToSpend
        if(signatures > 3)  {
          return false
        }
      }
    })
    return true
  }


  /**
    * Checks if each input script pushes only data and not opcodes to the evaluation stack
    * @return true if pushes only data, false otherwise
    */
  private def hasStandardPushData : Boolean =  {
    for(input <- inputs) {
      val chunks = input.inScript.getChunks.asScala
      chunks.foreach(chunk => {
        if (!input.isCoinbase && !chunk.isPushData) {
          return false
        }
      })
    }
    return true
  }


  /**
    * Checks if all conditions are evaluated true
    * @return true if all conditions are respected, false otherwise
    */
  private def hasStandardConditions : Boolean = {
    val format = new java.text.SimpleDateFormat("dd-MM-yyyy")
    if(date.after(format.parse("27-09-2014"))){
      return hasStandardTransactionSize && hasStandardSignatures && hasStandardScriptSize && hasStandardPushData
    }
    return true
  }
}

/**
  * Enumeration of all possible transaction types
  */
object TxType extends Enumeration {
  type TxType = Value
  val TX_NOTYPE,
  TX_STANDARD,
  TX_NONSTANDARD = Value
}

/**
  * Factories for [[tcs.blockchain.bitcoin.BitcoinTransaction]] instances.
  */
object BitcoinTransaction {
  /**
    * Factory for [[tcs.blockchain.bitcoin.BitcoinTransaction]] instances.
    * Creates a new transaction given its BitcoinJ representation.
    * Values of each appended BitcoinInput will be set to 0.
    *
    * @param tx BitcoinJ representation of the transaction
    * @return A new BitcoinTransaction
    */

  def factory(tx : Transaction) : BitcoinTransaction = {
    val inputs: List[BitcoinInput] = tx.getInputs.asScala.map(i => BitcoinInput.factory(i)).toList
    val outputs: List[BitcoinOutput] = tx.getOutputs.asScala.map(o => BitcoinOutput.factory(o)).toList

    // TODO: Test getMessageSize
    return new BitcoinTransaction(tx.getHash.toString, null, tx.getMessageSize, inputs, outputs, tx.getLockTime)


  }
  /**
    * Factory for [[tcs.blockchain.bitcoin.BitcoinTransaction]] instances.
    * Creates a new transaction given its BitcoinJ representation.
    * Values of each appended BitcoinInput will be set to 0.
    *
    * @param tx BitcoinJ representation of the transaction
    * @param txDate Date in which the containing block has been published
    * @return A new BitcoinTransaction
    */
  def factory(tx : Transaction, txDate : Date): BitcoinTransaction = {
    val inputs: List[BitcoinInput] = tx.getInputs.asScala.map(i => BitcoinInput.factory(i)).toList
    val outputs: List[BitcoinOutput] = tx.getOutputs.asScala.map(o => BitcoinOutput.factory(o)).toList

    // TODO: Test getMessageSize
    return new BitcoinTransaction(tx.getHash.toString, txDate, tx.getMessageSize, inputs, outputs, tx.getLockTime)
  }

  /**
    * Factory for [[tcs.blockchain.bitcoin.BitcoinTransaction]] instances.
    * Creates a new transaction given its BitcoinJ representation.
    * Values of each appended BitcoinInput will be set to the correct value
    * by exploiting the UTXO map provided.
    *
    * @param tx BitcoinJ representation of the transaction
    * @param txDate Date in which the containing block has been published
    * @param UTXOmap Unspent transaction outputs map
    * @param blockHeight Height of the enclosing block
    * @return A new BitcoinTransaction
    */
  def factory(tx: Transaction, txDate : Date, UTXOmap: mutable.HashMap[(Sha256Hash, Long), Long], blockHeight: Long): BitcoinTransaction = {
    val inputs: List[BitcoinInput] = tx.getInputs.asScala.map(i => BitcoinInput.factory(i, UTXOmap, blockHeight, tx.getOutputs.asScala.toList)).toList
    val outputs: List[BitcoinOutput] = tx.getOutputs.asScala.map(o => BitcoinOutput.factory(o, tx.getHash, UTXOmap)).toList

    // TODO: Test getMessageSize
    return new BitcoinTransaction(tx.getHash.toString, txDate, tx.getMessageSize, inputs, outputs, tx.getLockTime)
  }
}